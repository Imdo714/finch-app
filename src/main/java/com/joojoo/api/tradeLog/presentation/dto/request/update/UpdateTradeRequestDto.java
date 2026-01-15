package com.joojoo.api.tradeLog.presentation.dto.request.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joojoo.api.common.domain.enums.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class UpdateTradeRequestDto {

    @NotNull(message = "매매일지 ID(tradeLogId)는 필수입니다.")
    private Long tradeLogId;

    @NotNull(message = "매매 타입은 (BUY/SELL)로 작성해야 합니다.")
    private TradeType tradeType;

    @DecimalMin(value = "0.0", inclusive = false, message = "금액은 0보다 커야 합니다.")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "수량은 0보다 커야 합니다.")
    private BigDecimal amount;

    private String memo;
    private String riskFactor;
    private String tradingPlan;

    @PastOrPresent(message = "매매 일시는 미래일 수 없습니다.")
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    private LocalDateTime executedAt;

    public boolean isMetadataRelatedFieldsChanged(UpdateTradeRequestDto dto) {
        return (memo != null && !memo.trim().isEmpty()) ||
                (riskFactor != null && !riskFactor.trim().isEmpty()) ||
                (tradingPlan != null && !tradingPlan.trim().isEmpty());
    }

}
