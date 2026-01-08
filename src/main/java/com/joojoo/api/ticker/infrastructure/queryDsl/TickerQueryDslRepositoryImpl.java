package com.joojoo.api.ticker.infrastructure.queryDsl;

import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
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
    public RelatedBlockDetailCountResponse getTickerDetailCount(Long userId, Long tickerId) {
        return queryFactory
                .select(Projections.constructor(RelatedBlockDetailCountResponse.class,
                        ticker.name,
                        blockTicker.block.id.countDistinct().coalesce(0L),
                        blockTicker.tradeLog.id.countDistinct().coalesce(0L)
                ))
                .from(ticker)
                .leftJoin(blockTicker).on(
                        blockTicker.ticker.id.eq(ticker.id)
                                .and(blockTicker.userId.eq(userId))
                )
                .where(
                        ticker.id.eq(tickerId)
                )
                .groupBy(ticker.id, ticker.name)
                .fetchOne();
    }

}
