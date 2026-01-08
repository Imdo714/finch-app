package com.joojoo.api.tradeLog.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joojoo.api.common.domain.enums.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
@AllArgsConstructor
public class TradeRequestDto {

    @NotNull(message = "매매 타입(BUY/SELL)은 필수입니다.")
    private TradeType tradeType; // BUY, SELL

    @NotNull(message = "주식 ID(tickerId)는 필수입니다.")
    private Long tickerId;

    @NotNull(message = "금액은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "금액은 0보다 커야 합니다.")
    private BigDecimal price;

    @NotNull(message = "수량은 필수입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다.")
    private BigDecimal amount;

    private String memo; // 근거
    private String riskFactor; // 리스크
    private String tradingPlan; // 계획

    @NotNull(message = "매매 일시는 필수입니다.")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime executedAt; // 언제 매매 했는지
}
