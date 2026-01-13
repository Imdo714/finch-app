package com.joojoo.api.tradeLog.presentation.dto.request.calculate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TradeCalculateRequest {
    @NotNull(message = "대상 종목(Ticker) 정보는 필수입니다.")
    private Long tickerId;

    @NotNull(message = "현재가는 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "현재가는 0보다 커야 합니다.")
    @Digits(integer = 12, fraction = 4, message = "현재가는 정수 12자리, 소수점 4자리 이내여야 합니다.")
    private BigDecimal currentPrice;
}
