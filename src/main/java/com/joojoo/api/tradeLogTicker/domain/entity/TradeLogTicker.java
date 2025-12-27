package com.joojoo.api.tradeLogTicker.domain.entity;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trade_log_tickers")
public class TradeLogTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_log_id")
    private TradeLog tradeLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id")
    private Ticker ticker;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "field_type")
    private String fieldType;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Builder
    public TradeLogTicker(TradeLog tradeLog, Ticker ticker, Long userId, String fieldType, Integer sequence, Integer startOffset) {
        this.tradeLog = tradeLog;
        this.ticker = ticker;
        this.userId = userId;
        this.fieldType = fieldType;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }

    public static TradeLogTicker create(TradeLog tradeLog, Ticker ticker, Long userId, String fieldType, int start, int sequence) {
        return TradeLogTicker.builder()
                .tradeLog(tradeLog)
                .ticker(ticker)
                .userId(userId)
                .fieldType(fieldType)
                .startOffset(start)
                .sequence(sequence)
                .build();
    }
}
