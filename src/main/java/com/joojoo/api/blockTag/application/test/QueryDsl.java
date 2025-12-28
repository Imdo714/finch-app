package com.joojoo.api.blockTag.application.test;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueryDsl {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QTicker ticker = QTicker.ticker;
    private final QBlockTicker blockTicker = QBlockTicker.blockTicker;

    public List<BlockTag> findAllByTradeLogIds(List<Long> tradeLogIds) {
        if (tradeLogIds.isEmpty()) return Collections.emptyList();
        return queryFactory
                .selectFrom(blockTag)
                .join(blockTag.tag).fetchJoin()
                .where(blockTag.tradeLog.id.in(tradeLogIds))
                .fetch();
    }

    public List<BlockTicker> findAllTickersByTradeLogIds(List<Long> tradeLogIds) {
        if (tradeLogIds.isEmpty()) return Collections.emptyList();

        return queryFactory
                .selectFrom(blockTicker)
                .join(blockTicker.ticker, ticker).fetchJoin()
                .where(blockTicker.tradeLog.id.in(tradeLogIds))
                .fetch();
    }

}
