package com.joojoo.api.blockTicker.infrastructure.queryDsl;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.response.recent.RecentTickersResponse;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTickerQueryDslRepositoryImpl implements BlockTickerQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;
    private final QTicker ticker = QTicker.ticker;

    @Override
    public List<BlockTicker> findAllBlockTickers(List<Long> blockIds) {
        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(blockTicker.block.id.in(blockIds))
                .fetch();
    }

    @Override
    public List<BlockTicker> findAllTickersByTradeLogIds(List<Long> tradeLogIds) {
        if (tradeLogIds.isEmpty()) return Collections.emptyList();

        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(blockTicker.tradeLog.id.in(tradeLogIds))
                .fetch();
    }

    @Override
    public List<BlockTicker> findAllTickersByBlockId(Long blockId) {
        if (blockId == null) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(blockTicker.block.id.eq(blockId))
                .fetch();
    }

    @Override
    public List<BlockTicker> findAllTickersByBlockIdIn(List<Long> blockIds) {
        if (blockIds == null || blockIds.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(blockTicker.block.id.in(blockIds))
                .fetch();
    }

    @Override
    public List<RecentTickersResponse.RecentTickersDto> findRecentTickers(Long userId) {
        return queryFactory
                .select(Projections.constructor(RecentTickersResponse.RecentTickersDto.class,
                        ticker.id,
                        ticker.name,
                        ticker.symbol
                ))
                .from(blockTicker)
                .join(blockTicker.ticker, ticker)
                .where(blockTicker.userId.eq(userId))
                .groupBy(ticker.id)
                .orderBy(blockTicker.id.max().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public void deleteByTradeLogId(Long userId, Long tradeLogId) {
        queryFactory.delete(blockTicker)
                .where(
                        blockTicker.userId.eq(userId),
                        blockTicker.tradeLog.id.eq(tradeLogId)
                )
                .execute();
    }
}
