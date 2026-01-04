package com.joojoo.api.blockTag.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.enums.TagSourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "block_tags")
public class BlockTag  {

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public BlockTag(Tag tag, Block block, TradeLog tradeLog, Long userId, TagSourceType fieldType, Integer sequence, Integer startOffset, LocalDateTime createdAt) {
        this.tag = tag;
        this.block = block;
        this.tradeLog = tradeLog;
        this.userId = userId;
        this.fieldType = fieldType;
        this.sequence = sequence;
        this.startOffset = startOffset;
        this.createdAt = createdAt;
    }

    public static BlockTag create(Block block, Tag tag, Long userId, int start, int tagSeq, TagSourceType fieldType) {
        return BlockTag.builder()
                .block(block)
                .tag(tag)
                .userId(userId)
                .sequence(tagSeq)
                .startOffset(start)
                .fieldType(fieldType)
                .createdAt(block.getCreatedAt())
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
                .createdAt(tradeLog.getCreatedAt())
                .build();
    }

    /** 엔티티로부터 생성일자를 추출하는 메서드 */
    public LocalDate getRelevantCreatedDate() {
        if (this.block != null) {
            return this.block.getCreatedAt().toLocalDate();
        }
        if (this.tradeLog != null) {
            return this.tradeLog.getCreatedAt().toLocalDate();
        }
        return null;
    }

}
