package com.joojoo.api.blockTag.infrastructure.queryDsl.date;

import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTagDateQueryDslRepositoryImpl implements BlockTagDateQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;

    @Override
    public TagDateResult findAllByTagAndDate(Long userId, Long tagId, LocalDate lastDate) {
        int dateCount = 3;
        List<LocalDate> targetDates = getTargetDates(userId, tagId, lastDate, dateCount);

        if (targetDates.isEmpty()) return new TagDateResult(Collections.emptyList(), targetDates);

        // 조회된 날짜에서 가장 과거 날짜
        LocalDate minDate = targetDates.get(targetDates.size() - 1);

        // 실제 데이터 조회
        List<BlockTag> content = queryFactory
                .selectFrom(blockTag)
                .leftJoin(blockTag.block, block).fetchJoin()
                .leftJoin(blockTag.tradeLog, tradeLog).fetchJoin()
                .where(
                        blockTag.tag.id.eq(tagId),
                        blockTag.userId.eq(userId),
                        blockTag.createdAt.goe(minDate.atStartOfDay()),
                        leLastDate(lastDate)
                )
                .orderBy(blockTag.createdAt.desc())
                .fetch();

        return new TagDateResult(content, targetDates);
    }

    private List<LocalDate> getTargetDates(Long userId, Long tagId, LocalDate lastDate, int dateCount) {
        return queryFactory
                .select(block.createdAt)
                .from(blockTag)
                .where(
                        blockTag.tag.id.eq(tagId),
                        blockTag.userId.eq(userId),
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

    private BooleanExpression leLastDate(LocalDate lastDate) {
        if (lastDate == null) return null;
        return blockTag.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }


}
