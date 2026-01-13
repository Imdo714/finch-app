package com.joojoo.api.tradeLog.domain.service;

import com.joojoo.api.tradeLog.presentation.dto.response.calculate.TradeMetricsResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Getter
@NoArgsConstructor
public class TradeMetrics {
    private BigDecimal currentQuantity;     // 현재 보유 수량 (매수합 - 매도합)
    private BigDecimal totalPurchaseAmount; // 보유 중인 주식의 총 매수 원가
    private BigDecimal averagePrice;        // 평균 매수가

    private BigDecimal calculateAveragePrice(BigDecimal buyQty, BigDecimal buyAmount) {
        if (buyQty == null || buyQty.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return buyAmount.divide(buyQty, 2, RoundingMode.HALF_UP);
    }

    public TradeMetrics(BigDecimal totalBuyQty, BigDecimal totalSellQty, BigDecimal totalBuyAmount) {
        // 현재 보유 수량 계산
        BigDecimal buyQty = totalBuyQty != null ? totalBuyQty : BigDecimal.ZERO;
        BigDecimal sellQty = totalSellQty != null ? totalSellQty : BigDecimal.ZERO;

        this.currentQuantity = buyQty.subtract(sellQty);

        // 총 매수 원가 (평단가 계산용)
        this.totalPurchaseAmount = totalBuyAmount != null ? totalBuyAmount : BigDecimal.ZERO;

        // 평균 매수가 계산
        this.averagePrice = calculateAveragePrice(buyQty, totalBuyAmount);
    }

    public TradeMetricsResponse calculate(BigDecimal currentPrice) {
        if (currentPrice == null) return TradeMetricsResponse.empty();

        BigDecimal evaluationAmount = calculateEvaluationAmount(currentPrice);  // 평가 금액
        BigDecimal investedAmount = calculateInvestedAmount();                  // 투자 원가 계산
        BigDecimal profitLoss = calculateProfitLoss(evaluationAmount, investedAmount); // 손익
        BigDecimal yield = calculateYield(profitLoss, investedAmount);          // 수익률

        return TradeMetricsResponse.of(this.currentQuantity, this.averagePrice, profitLoss, yield);
    }

    // 평가 금액 계산: 현재가 * 보유 수량
    private BigDecimal calculateEvaluationAmount(BigDecimal currentPrice) {
        return currentPrice.multiply(this.currentQuantity);
    }

    // 실제 투자 원가 계산: 평균 매수가 * 현재 보유 수량
    private BigDecimal calculateInvestedAmount() {
        return this.averagePrice.multiply(this.currentQuantity);
    }

    // 평가 손익 계산: 평가 금액 - 투자 원가
    private BigDecimal calculateProfitLoss(BigDecimal evaluationAmount, BigDecimal investedAmount) {
        return evaluationAmount.subtract(investedAmount);
    }

    // 수익률 계산: (손익 / 원가) * 100
    private BigDecimal calculateYield(BigDecimal profitLoss, BigDecimal investedAmount) {
        if (investedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return profitLoss.divide(investedAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

}
