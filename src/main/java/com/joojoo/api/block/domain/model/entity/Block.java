package com.joojoo.api.block.domain.model.entity;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer depth;

    @Column(nullable = false)
    private Integer sequence;

    @Column(name = "is_saved", nullable = false)
    private Boolean isSaved;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockTag> blockTags = new ArrayList<>();

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockTicker> blockTickers = new ArrayList<>();

    @Builder
    public Block(User user, Block parent, String content, Integer depth, Integer sequence, Boolean isSaved) {
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.depth = depth;
        this.sequence = sequence;
        this.isSaved = (isSaved != null) ? isSaved : false;
    }

    public static Block createBlockBuild(User user, Block parentBlock, BlockRequestDto dto){
        return Block.builder()
                .user(user)
                .parent(parentBlock)
                .content(dto.getContent())
                .depth(dto.getDepth())
                .sequence(dto.getSequence())
                .isSaved(dto.getIsSaved())
                .build();
    }


    public void promote(Block newParent, int newSequence) {
        this.parent = newParent;
        this.sequence = newSequence;

        int targetDepth = (newParent == null) ? 0 : newParent.getDepth() + 1;
        this.updateDepthRecursive(targetDepth);
    }

    private void updateDepthRecursive(int newDepth) {
        this.depth = newDepth;
        for (Block child : children) {
            child.updateDepthRecursive(newDepth + 1);
        }
    }

    /** 자신을 포함한 모든 하위 자손 ID 수집 (재귀) */
    public void collectAllIds(List<Long> ids) {
        ids.add(this.id);
        for (Block child : children) {
            child.collectAllIds(ids);
        }
    }

    public void disconnectChildren() {
        this.children.clear();
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
