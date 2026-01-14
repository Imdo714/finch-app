package com.joojoo.api.tradeLog.infrastructure.queryDsl;

import com.joojoo.api.common.domain.enums.TradeType;
import com.joojoo.api.tradeLog.domain.model.entity.QTradeLog;
import com.joojoo.api.tradeLog.domain.service.calculate.TradeMetrics;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

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
    public void deleteByTradeLogId(Long userId, Long tradeLogId) {
        queryFactory
                .delete(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        tradeLog.id.eq(tradeLogId)
                )
                .execute();
    }

    @Override
    public TradeMetrics findAllBuyLogsByTicker(Long userId, Long tickerId) {
        CaseBuilder caseBuilder = new CaseBuilder();

        return queryFactory
                .select(Projections.constructor(TradeMetrics.class,
                        // 매수 수량 합계: BUY일 때만 amount, 아니면 0
                        caseBuilder.when(tradeLog.tradeType.eq(TradeType.BUY))
                                .then(tradeLog.amount)
                                .otherwise(BigDecimal.ZERO)
                                .sum(),

                        // 매도 수량 합계: SELL일 때만 amount, 아니면 0
                        caseBuilder.when(tradeLog.tradeType.eq(TradeType.SELL))
                                .then(tradeLog.amount)
                                .otherwise(BigDecimal.ZERO)
                                .sum(),

                        // 총 매수 금액 합계: BUY일 때만 (가격 * 수량), 아니면 0
                        caseBuilder.when(tradeLog.tradeType.eq(TradeType.BUY))
                                .then(tradeLog.price.multiply(tradeLog.amount))
                                .otherwise(BigDecimal.ZERO)
                                .sum()
                ))
                .from(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        tradeLog.ticker.id.eq(tickerId)
                )
                .fetchOne();
    }

    @Override
    public List<ChartOverlayResponse.TradeDetailDto> getTradeBuySellRecords(Long userId, Long tickerId) {
        return queryFactory
                .select(Projections.constructor(ChartOverlayResponse.TradeDetailDto.class,
                        tradeLog.id,
                        tradeLog.executedAt,
                        tradeLog.tradeType
                ))
                .from(tradeLog)
                .where(
                        tradeLog.user.id.eq(userId),
                        tradeLog.ticker.id.eq(tickerId),
                        tradeLog.isDeleted.isFalse()
                )
                .fetch();
    }

}
