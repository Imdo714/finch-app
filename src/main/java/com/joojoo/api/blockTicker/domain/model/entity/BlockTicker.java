package com.joojoo.api.blockTicker.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.enums.TagSourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "block_tickers")
public class BlockTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private Ticker ticker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_log_id")
    private TradeLog tradeLog;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    private TagSourceType fieldType;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public BlockTicker(Block block, Ticker ticker, TradeLog tradeLog, Long userId, TagSourceType fieldType, Integer sequence, Integer startOffset, LocalDateTime createdAt) {
        this.block = block;
        this.ticker = ticker;
        this.tradeLog = tradeLog;
        this.userId = userId;
        this.fieldType = fieldType;
        this.sequence = sequence;
        this.startOffset = startOffset;
        this.createdAt = createdAt;
    }

    public static BlockTicker create(Block block, Ticker ticker, Long userId, int start, int tSeq, TagSourceType fieldType) {
        return BlockTicker.builder()
                .block(block)
                .ticker(ticker)
                .userId(userId)
                .sequence(tSeq)
                .startOffset(start)
                .fieldType(fieldType)
                .createdAt(block.getCreatedAt())
                .build();
    }

    public static BlockTicker create(TradeLog tradeLog, Ticker ticker, Long userId, TagSourceType fieldType, Integer startOffset, Integer sequence) {
        return BlockTicker.builder()
                .tradeLog(tradeLog)
                .ticker(ticker)
                .userId(userId)
                .fieldType(fieldType)
                .startOffset(startOffset)
                .sequence(sequence)
                .createdAt(tradeLog.getCreatedAt())
                .build();
    }
}
