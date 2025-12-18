package com.joojoo.api.block.presentation.dto.request.createBlock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockRequestDto {
    private String content;
    private Integer depth;
    private Integer sequence;
    private Boolean isSaved;
    private List<BlockRequestDto> children;
}
