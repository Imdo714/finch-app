package com.joojoo.api.block.domain.model.entity;

import com.joojoo.api.block.domain.model.enums.BlockType;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blocks")
public class Block extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Block parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Block> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private TradeLog tradeLog;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer depth;

    @Column(nullable = false)
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockType type;

    @Column(name = "is_saved", nullable = false)
    private Boolean isSaved = false;

    @Builder
    public Block(User user, Block parent, String content, Integer depth, Integer sequence, BlockType type) {
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.depth = depth;
        this.sequence = sequence;
        this.type = type;
    }
}
