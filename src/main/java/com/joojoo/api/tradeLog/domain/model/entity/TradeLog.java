package com.joojoo.api.tradeLog.domain.model.entity;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.entity.BaseTimeEntity;
import com.joojoo.global.common.enums.TradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

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

    // JSON 처리 (Hibernate 6 기준)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "json", nullable = false)
    private Map<String, Object> content;

    @Builder
    public TradeLog(User user, Ticker ticker, TradeType tradeType, BigDecimal price, BigDecimal amount, String currency, LocalDateTime executedAt, Map<String, Object> content) {
        this.user = user;
        this.ticker = ticker;
        this.tradeType = tradeType;
        this.price = price;
        this.amount = amount;
        this.currency = currency;
        this.executedAt = executedAt;
        this.content = content;
    }
}
