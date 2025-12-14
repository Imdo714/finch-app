package com.joojoo.api.blockTicker.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
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
public class BlockTicker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker_id", nullable = false)
    private Ticker ticker;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public BlockTicker(Block block, Ticker ticker, Integer sequence, Integer startOffset) {
        this.block = block;
        this.ticker = ticker;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }
}
