package com.joojoo.api.blockTicker.infrastructure.queryDsl;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.model.entity.QBlockTicker;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
