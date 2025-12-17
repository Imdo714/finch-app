package com.joojoo.api.block.presentation.dto.request.createBlock;

import com.joojoo.api.block.domain.model.enums.BlockType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockRequestDto {
    private String content;
    private BlockType blockType;
    private Integer depth;
    private Integer sequence;
    private Boolean isSaved;
    private List<BlockRequestDto> children;
}
