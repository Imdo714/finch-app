package com.joojoo.api.blockTag.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.tag.domain.model.entity.Tag;
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
@Table(name = "block_tags")
public class BlockTag extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id")
    private Block block;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_log_id")
    private TradeLog tradeLog;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Builder
    public BlockTag(Tag tag, Block block, TradeLog tradeLog, Integer sequence, Integer startOffset) {
        this.tag = tag;
        this.block = block;
        this.tradeLog = tradeLog;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }

    public static BlockTag create(Block block, Tag tag, int start, int tagSeq) {
        return BlockTag.builder()
                .block(block)
                .tag(tag)
                .sequence(tagSeq)
                .startOffset(start)
                .build();
    }
}
