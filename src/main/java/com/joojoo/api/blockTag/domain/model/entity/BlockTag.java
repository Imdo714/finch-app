package com.joojoo.api.blockTag.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.entity.BaseCreateEntity;
import com.joojoo.global.common.enums.TagSourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type")
    private TagSourceType fieldType;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Builder
    public BlockTag(Tag tag, Block block, TradeLog tradeLog, Long userId, TagSourceType fieldType, Integer sequence, Integer startOffset) {
        this.tag = tag;
        this.block = block;
        this.tradeLog = tradeLog;
        this.userId = userId;
        this.fieldType = fieldType;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }

    public static BlockTag create(Block block, Tag tag, Long userId, int start, int tagSeq, TagSourceType fieldType) {
        return BlockTag.builder()
                .block(block)
                .tag(tag)
                .userId(userId)
                .sequence(tagSeq)
                .startOffset(start)
                .fieldType(fieldType)
                .build();
    }

    public static BlockTag create(TradeLog tradeLog, Tag tag, Long userId, TagSourceType fieldType, Integer startOffset, Integer sequence) {
        return BlockTag.builder()
                .tradeLog(tradeLog)
                .tag(tag)
                .userId(userId)
                .fieldType(fieldType)
                .startOffset(startOffset)
                .sequence(sequence)
                .build();
    }
}
