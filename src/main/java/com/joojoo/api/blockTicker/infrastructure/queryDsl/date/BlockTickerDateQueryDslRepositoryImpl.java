package com.joojoo.api.blockTicker.infrastructure.queryDsl.date;

import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
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
public class BlockTickerDateQueryDslRepositoryImpl implements BlockTickerDateQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;

    @Override
    public TickerDateResult findAllByTickerAndDate(Long userId, Long tickerId, LocalDate targetDate) {
        int dateCount = 3;
        List<LocalDate> targetDates = getTargetTickerDates(userId, tickerId, targetDate, dateCount);

        if (targetDates.isEmpty()) return new TickerDateResult(Collections.emptyList(), targetDates);

        // 조회된 날짜에서 가장 과거 날짜
        LocalDate minDate = targetDates.get(targetDates.size() - 1);

        // 실제 데이터 조회
        List<BlockTicker> content = queryFactory
                .selectFrom(blockTicker)
                .leftJoin(blockTicker.block, block).fetchJoin()
                .leftJoin(blockTicker.tradeLog, tradeLog).fetchJoin()
                .where(
                        blockTicker.ticker.id.eq(tickerId),
                        blockTicker.userId.eq(userId),
                        blockTicker.createdAt.goe(minDate.atStartOfDay()),
                        leLastTickerDate(targetDate)
                )
                .orderBy(blockTicker.createdAt.desc())
                .fetch();

        return new TickerDateResult(content, targetDates);
    }

    private List<LocalDate> getTargetTickerDates(Long userId, Long tickerId, LocalDate lastDate, int dateCount) {
        return queryFactory
                .select(blockTicker.createdAt)
                .from(blockTicker)
                .where(
                        blockTicker.ticker.id.eq(tickerId),
                        blockTicker.userId.eq(userId),
                        leLastTickerDate(lastDate)
                )
                .orderBy(blockTicker.createdAt.desc())
                .fetch()
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .limit(dateCount)
                .toList();
    }

    private BooleanExpression leLastTickerDate(LocalDate lastDate) {
        if (lastDate == null) return null;
        return blockTicker.createdAt.lt(lastDate.plusDays(1).atStartOfDay());
    }
}
