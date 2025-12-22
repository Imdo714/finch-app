package com.joojoo.api.block.infrastructure.queryDsl.date;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.entity.QBlock;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class blockQueryDslDateRepositoryImpl implements blockQueryDslDateRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlock block = QBlock.block;

    @Override
    public List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount) {
        List<LocalDate> targetDates = getTargetDates(userId, lastDate, dateCount);

        if (targetDates.isEmpty()) return Collections.emptyList();

        // 조회된 날짜에서 가장 과거 날짜
        LocalDate minDate = targetDates.get(targetDates.size() - 1);

        // 해당 범위 내의 모든 블록 데이터 조회 (Index Range Scan 활용)
        return findBlocksInRange(userId, minDate, lastDate);
    }

    @Override
    public LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult) {
        LocalDateTime nextTime = queryFactory
                .select(block.createdAt)
                .from(block)
                .where(
                        isOwner(userId),
                        isRoot(),
                        block.createdAt.lt(oldestDateInResult.atStartOfDay())
                )
                .orderBy(block.createdAt.desc())
                .fetchFirst();

        return nextTime != null ? nextTime.toLocalDate() : null;
    }

    /** 데이터가 존재하는 고유한 날짜(LocalDate) 리스트를 최신순으로 추출 */
    private List<LocalDate> getTargetDates(Long userId, LocalDate lastDate, int dateCount) {
        return queryFactory
                .select(block.createdAt)
                .from(block)
                .where(
                        isOwner(userId),
                        isRoot(),
                        leLastDate(lastDate)
                )
                .orderBy(block.createdAt.desc())
                .fetch()
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .limit(dateCount)
                .toList();
    }

    /** 특정 날짜 범위(minDate ~ lastDate) 내의 모든 루트 블록 조회 */
    private List<Block> findBlocksInRange(Long userId, LocalDate minDate, LocalDate maxDate) {
        return queryFactory
                .selectFrom(block)
                .where(
                        isOwner(userId),
                        isRoot(),
                        block.createdAt.goe(minDate.atStartOfDay()),
                        leLastDate(maxDate)
                )
                .orderBy(block.createdAt.desc())
                .fetch();
    }

    private BooleanExpression isOwner(Long userId) {
        return block.user.id.eq(userId);
    }

    private BooleanExpression isRoot() {
        return block.parent.isNull();
    }

    private BooleanExpression leLastDate(LocalDate lastDate) {
        if (lastDate == null) return null;
        // block.createdAt >= 21일 00:00
        return block.createdAt.loe(lastDate.atTime(LocalTime.MAX));
    }

}
