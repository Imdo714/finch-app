package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class tradeLogQueryDslRepositoryImpl implements tradeLogQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QTradeLog tradeLog = QTradeLog.tradeLog;

    @Override
    public void withdrawByUserId(Long userId) {
        queryFactory
                .delete(tradeLog)
                .where(tradeLog.user.id.eq(userId))
                .execute();
    }

    @Override
    public void deleteByTradeLogId(Long tradeLogId) {
        queryFactory
                .delete(tradeLog)
                .where(tradeLog.id.eq(tradeLogId))
                .execute();
    }
}
