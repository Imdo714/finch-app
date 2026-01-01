package com.joojoo.api.ticker.infrastructure.queryDsl;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TickerQueryDslRepositoryImpl implements TickerQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QTicker ticker = QTicker.ticker;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;

    @Override
    public List<Ticker> findAllByTickerNames(Set<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            return Collections.emptyList();
        }

        return queryFactory
                .selectFrom(ticker)
                .where(ticker.name.in(symbols))
                .fetch();
    }

    @Override
    public BlockTagCountResponse getTickerDetailCount(Long userId, Long tickerId) {
        return queryFactory
                .select(Projections.constructor(BlockTagCountResponse.class,
                        ticker.name,
                        blockTicker.block.id.countDistinct(),
                        blockTicker.tradeLog.id.countDistinct()
                ))
                .from(blockTicker)
                .join(blockTicker.ticker, ticker)
                .where(
                        ticker.id.eq(tickerId),
                        blockTicker.userId.eq(userId)
                )
                .fetchOne();
    }

}
