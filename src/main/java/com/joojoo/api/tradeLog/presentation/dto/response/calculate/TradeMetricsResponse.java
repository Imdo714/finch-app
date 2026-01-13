package com.joojoo.api.tradeLog.presentation.dto.response.calculate;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Builder
@Getter
@AllArgsConstructor
public class TradeMetricsResponse {
    private String currentQuantity; // 현재 보유 수량
    private String averagePrice;    // 평균 매수가
    private String profitLoss;      // 평가 손익
    private String yield;           // 수익률 (%)

    public static TradeMetricsResponse empty() {
        return new TradeMetricsResponse("0", "0", "0", "0");
    }

    public static TradeMetricsResponse of(BigDecimal currentQuantity, BigDecimal averagePrice, BigDecimal profitLoss, BigDecimal yield){
        return TradeMetricsResponse.builder()
                .currentQuantity(currentQuantity.stripTrailingZeros().toPlainString())
                .averagePrice(averagePrice.stripTrailingZeros().toPlainString())
                .profitLoss(profitLoss.stripTrailingZeros().toPlainString())
                .yield(yield.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString())
                .build();
    }

}
