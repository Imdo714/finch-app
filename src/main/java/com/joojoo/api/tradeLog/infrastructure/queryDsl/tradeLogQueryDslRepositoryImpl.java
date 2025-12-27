package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.QBlock;
import com.joojoo.api.blockTag.domain.model.entity.QBlockTag;
import com.joojoo.api.tag.domain.model.entity.QTag;
import com.joojoo.api.ticker.domain.model.entity.QTicker;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.user.domain.model.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class tradeLogQueryDslRepositoryImpl implements tradeLogQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QBlockTag blockTag = QBlockTag.blockTag;
    private final QTag tag = QTag.tag;
    private final QBlock block = QBlock.block;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;
    private final QUser user = QUser.user;
    private final QTicker ticker = QTicker.ticker;

    @Override
    public List<TradeLog> findAllByIdIn(List<Long> tradeLogIds) {
        if (tradeLogIds.isEmpty()) return Collections.emptyList();

        return queryFactory
                .selectFrom(tradeLog)
                .leftJoin(tradeLog.ticker, ticker).fetchJoin() // 종목 정보(티커) 미리 가져오기
                .where(tradeLog.id.in(tradeLogIds))
                .orderBy(tradeLog.executedAt.desc(), tradeLog.id.desc())
                .fetch();
    }
}
