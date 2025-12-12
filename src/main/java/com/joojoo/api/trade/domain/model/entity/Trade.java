package com.joojoo.api.trade.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.user.domain.model.enums.Currency;
import com.joojoo.global.common.enums.TradeType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trades")
public class Trade {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private Ticker ticker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType type; // BUY, SELL

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal price;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt; // 거래시간

    @Builder
    public Trade(Block block, Ticker ticker, TradeType type, BigDecimal price, BigDecimal amount, Currency currency, LocalDateTime executedAt) {
        this.block = block;
        this.ticker = ticker;
        this.type = type;
        this.price = price;
        this.amount = amount;
        this.currency = currency;
        this.executedAt = executedAt;
    }
}
