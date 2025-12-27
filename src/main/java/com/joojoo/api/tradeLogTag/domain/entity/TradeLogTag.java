package com.joojoo.api.tradeLogTag.domain.entity;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "trade_log_tags")
public class TradeLogTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_log_id")
    private TradeLog tradeLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "field_type")
    private String fieldType;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @Builder
    public TradeLogTag(TradeLog tradeLog, Tag tag, Long userId, String fieldType, Integer sequence, Integer startOffset) {
        this.tradeLog = tradeLog;
        this.tag = tag;
        this.userId = userId;
        this.fieldType = fieldType;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }

    public static TradeLogTag create(TradeLog tradeLog, Tag tag, Long userId, String fieldType, int start, int sequence) {
        return TradeLogTag.builder()
                .tradeLog(tradeLog)
                .tag(tag)
                .userId(userId)
                .fieldType(fieldType)
                .startOffset(start)
                .sequence(sequence)
                .build();
    }
}
