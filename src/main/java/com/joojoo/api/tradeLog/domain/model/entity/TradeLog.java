package com.joojoo.api.tradeLog.domain.model.entity;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.common.domain.entity.BaseTimeEntity;
import com.joojoo.api.common.domain.enums.TagSourceType;
import com.joojoo.api.common.domain.enums.TradeType;
import com.joojoo.global.exception.handleException.tradeLog.NotTradeLogOwnerException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trade_logs")
public class TradeLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private Ticker ticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal price;   // 매수가/매도가

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal amount;  // 수량

    @Column(nullable = false, length = 10)
    private String currency;    // KRW, USD

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt; // 실제 매매 일시

    private String memo;

    @Column(name = "risk_factor")
    private String riskFactor;

    @Column(name = "trading_plan")
    private String tradingPlan;

    @Builder
    public TradeLog(User user, Ticker ticker, TradeType tradeType, BigDecimal price, BigDecimal amount, String currency, LocalDateTime executedAt,
                    String memo, String riskFactor, String tradingPlan) {
        this.user = user;
        this.ticker = ticker;
        this.tradeType = tradeType;
        this.price = price;
        this.amount = amount;
        this.currency = currency;
        this.executedAt = executedAt;
        this.memo = memo;
        this.riskFactor = riskFactor;
        this.tradingPlan = tradingPlan;
    }

    public static TradeLog create(User user, Ticker ticker, TradeRequestDto tradeRequestDto){
        return TradeLog.builder()
                .user(user)
                .ticker(ticker)
                .tradeType(tradeRequestDto.getTradeType())
                .price(tradeRequestDto.getPrice())
                .amount(tradeRequestDto.getAmount())
                .currency("KRW")
                .executedAt(tradeRequestDto.getExecutedAt())
                .memo(tradeRequestDto.getMemo())
                .riskFactor(tradeRequestDto.getRiskFactor())
                .tradingPlan(tradeRequestDto.getTradingPlan())
                .build();
    }

    public Map<TagSourceType, String> getMetadataSources() {
        return Map.of(
                TagSourceType.TRADE_MEMO, Objects.requireNonNullElse(this.memo, ""),
                TagSourceType.TRADE_RISK, Objects.requireNonNullElse(this.riskFactor, ""),
                TagSourceType.TRADE_PLAN, Objects.requireNonNullElse(this.tradingPlan, "")
        );
    }

    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new NotTradeLogOwnerException();
        }
    }

}
