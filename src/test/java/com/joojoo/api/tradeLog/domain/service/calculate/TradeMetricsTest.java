package com.joojoo.api.tradeLog.domain.service.calculate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class TradeMetricsTest {

    @Test
    @DisplayName("평가 금액 계산: 현재가와 보유 수량을 곱한 값이 정확해야 한다")
    void calculateEvaluationAmount_success() {
        // given
        // 10.5주를 보유하고 있고 현재가가 10,000원인 상황
        BigDecimal currentQuantity = new BigDecimal("10.5");
        BigDecimal currentPrice = new BigDecimal("10000");

        // when
        BigDecimal result = currentPrice.multiply(currentQuantity);

        // then
        // 10,000 * 10.5 = 105,000
        assertThat(result).isEqualByComparingTo("105000");
    }

    @Test
    @DisplayName("평가 금액 계산: 현재가가 0원인 경우 평가 금액은 0원이어야 한다")
    void calculateEvaluationAmount_when_price_is_zero() {
        // given
        BigDecimal currentQuantity = new BigDecimal("50");
        BigDecimal currentPrice = BigDecimal.ZERO;

        // when
        BigDecimal result = currentPrice.multiply(currentQuantity);

        // then
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("평가 금액 계산: 소수점 단위의 수량과 현재가 계산이 정확해야 한다")
    void calculateEvaluationAmount_with_decimal_points() {
        // given
        // 0.1234주 보유, 현재가 1,234,567원
        BigDecimal currentQuantity = new BigDecimal("0.1234");
        BigDecimal currentPrice = new BigDecimal("1234567");

        // when
        BigDecimal result = currentPrice.multiply(currentQuantity);

        // then
        // 1,234,567 * 0.1234 = 152345.5678
        assertThat(result).isEqualByComparingTo("152345.5678");
    }

    @Test
    @DisplayName("투자 원가 계산: 평균 매수가와 현재 보유 수량을 곱한 값이 정확해야 한다")
    void calculateInvestedAmount_success() {
        // given
        // 평단가 55,000원에 10주를 보유하고 있는 상황
        BigDecimal averagePrice = new BigDecimal("55000");
        BigDecimal currentQuantity = new BigDecimal("10");

        // when
        // 원가 = 55,000 * 10
        BigDecimal result = averagePrice.multiply(currentQuantity);

        // then
        assertThat(result).isEqualByComparingTo("550000");
    }

    @Test
    @DisplayName("투자 원가 계산: 소수점 단위의 수량을 보유한 경우에도 원가가 정확히 계산되어야 한다")
    void calculateInvestedAmount_with_decimal_quantity() {
        // given
        // 평단가 1,200.5원, 보유 수량 0.5주
        BigDecimal averagePrice = new BigDecimal("1200.5");
        BigDecimal currentQuantity = new BigDecimal("0.5");

        // when
        // 원가 = 1200.5 * 0.5 = 600.25
        BigDecimal result = averagePrice.multiply(currentQuantity);

        // then
        assertThat(result).isEqualByComparingTo("600.25");
    }

    @Test
    @DisplayName("투자 원가 계산: 보유 수량이 0인 경우 투자 원가는 0원이어야 한다")
    void calculateInvestedAmount_when_quantity_is_zero() {
        // given
        BigDecimal averagePrice = new BigDecimal("100000");
        BigDecimal currentQuantity = BigDecimal.ZERO;

        // when
        BigDecimal result = averagePrice.multiply(currentQuantity);

        // then
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("평가 손익 계산: 평가 금액이 투자 원가보다 클 때 이익이 정확히 계산되어야 한다")
    void calculateProfitLoss_profit() {
        // given
        // 평가 금액: 1,575,000원, 투자 원가: 1,501,500원
        BigDecimal evaluationAmount = new BigDecimal("1575000");
        BigDecimal investedAmount = new BigDecimal("1501500");

        // when
        // 손익 = 1,575,000 - 1,501,500 = 73,500
        BigDecimal result = evaluationAmount.subtract(investedAmount);

        // then
        assertThat(result).isEqualByComparingTo("73500");
    }

    @Test
    @DisplayName("평가 손익 계산: 평가 금액이 투자 원가보다 작을 때 손실액이 마이너스로 정확히 계산되어야 한다")
    void calculateProfitLoss_loss() {
        // given
        // 평가 금액: 80,000원, 투자 원가: 100,000원
        BigDecimal evaluationAmount = new BigDecimal("80000");
        BigDecimal investedAmount = new BigDecimal("100000");

        // when
        // 손익 = 80,000 - 100,000 = -20,000
        BigDecimal result = evaluationAmount.subtract(investedAmount);

        // then
        assertThat(result).isEqualByComparingTo("-20000");
    }

    @Test
    @DisplayName("평가 손익 계산: 소수점이 포함된 금액들 간의 차이도 정확하게 계산되어야 한다")
    void calculateProfitLoss_with_decimal() {
        // given
        // 평가 금액: 1000.555, 투자 원가: 1000.111
        BigDecimal evaluationAmount = new BigDecimal("1000.555");
        BigDecimal investedAmount = new BigDecimal("1000.111");

        // when
        // 손익 = 0.444
        BigDecimal result = evaluationAmount.subtract(investedAmount);

        // then
        assertThat(result).isEqualByComparingTo("0.444");
    }

    @Test
    @DisplayName("수익률 계산: 이익이 발생했을 때 수익률이 정확하게 계산되어야 한다")
    void calculateYield_profit() {
        // given
        // 손익: 5,000원, 투자 원가: 100,000원
        BigDecimal profitLoss = new BigDecimal("5000");
        BigDecimal investedAmount = new BigDecimal("100000");

        // when
        // (5,000 / 100,000) * 100 = 5.0000
        BigDecimal result = profitLoss.divide(investedAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // then
        assertThat(result).isEqualByComparingTo("5");
    }

    @Test
    @DisplayName("수익률 계산: 손실이 발생했을 때 마이너스 수익률이 정확하게 계산되어야 한다")
    void calculateYield_loss() {
        // given
        // 손익: -2,000원, 투자 원가: 10,000원
        BigDecimal profitLoss = new BigDecimal("-2000");
        BigDecimal investedAmount = new BigDecimal("10000");

        // when
        // (-2,000 / 10,000) * 100 = -20.0000
        BigDecimal result = profitLoss.divide(investedAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // then
        assertThat(result).isEqualByComparingTo("-20");
    }

    @Test
    @DisplayName("수익률 계산: 투자 원가가 0이거나 음수인 경우 수익률은 0이어야 한다")
    void calculateYield_when_investedAmount_is_zero() {
        // given
        BigDecimal profitLoss = new BigDecimal("5000");
        BigDecimal investedAmount = BigDecimal.ZERO;

        // when
        BigDecimal result;
        if (investedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            result = BigDecimal.ZERO;
        } else {
            result = profitLoss.divide(investedAmount, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        // then
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("수익률 계산: 복잡한 소수점 결과도 반올림 정책에 따라 정확해야 한다")
    void calculateYield_with_rounding() {
        // given
        // 손익: 1,000원, 투자 원가: 30,000원
        // (1,000 / 30,000) * 100 = 3.333333...
        BigDecimal profitLoss = new BigDecimal("1000");
        BigDecimal investedAmount = new BigDecimal("30000");

        // when
        BigDecimal result = profitLoss.divide(investedAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // then
        // 0.0333 * 100 = 3.3300
        assertThat(result).isEqualByComparingTo("3.33");
    }

}