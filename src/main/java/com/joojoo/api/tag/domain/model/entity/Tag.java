package com.joojoo.api.tag.domain.model.entity;

import com.joojoo.api.block.domain.model.entity.Block;
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
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @Column(nullable = false)
    private String content;

    private Integer sequence;

    @Column(name = "start_offset")
    private Integer startOffset;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Tag(Block block, String content, Integer sequence, Integer startOffset) {
        this.block = block;
        this.content = content;
        this.sequence = sequence;
        this.startOffset = startOffset;
    }
}
