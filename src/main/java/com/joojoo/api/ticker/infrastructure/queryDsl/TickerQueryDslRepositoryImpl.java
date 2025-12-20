package com.joojoo.api.ticker.infrastructure.queryDsl;

import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
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

}
