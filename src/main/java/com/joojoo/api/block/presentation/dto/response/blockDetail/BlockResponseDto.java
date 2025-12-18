package com.joojoo.api.block.presentation.dto.response.blockDetail;

import com.joojoo.api.block.domain.model.entity.Block;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class BlockResponseDto {
    private Long blockId;
    private String content;
    private Integer depth;
    private Integer sequence;
    private List<BlockResponseDto> children;

    public static BlockResponseDto of(Block block, List<BlockResponseDto> children) {
        return BlockResponseDto.builder()
                .blockId(block.getId())
                .content(block.getContent())
                .depth(block.getDepth())
                .sequence(block.getSequence())
                .children(children)
                .build();
    }
}
