package com.joojoo.api.blockTicker.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.entity.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "block_tickers")
public class BlockTicker extends BaseCreateEntity {

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

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Builder
    public BlockTicker(Block block, Ticker ticker, TradeLog tradeLog, Integer sequence, Integer startOffset) {
        this.block = block;
        this.ticker = ticker;
        this.tradeLog = tradeLog;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }
}
