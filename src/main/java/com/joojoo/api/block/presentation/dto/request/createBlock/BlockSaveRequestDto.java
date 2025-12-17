package com.joojoo.api.block.presentation.dto.request.createBlock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockSaveRequestDto {
    private List<BlockRequestDto> blocks;
}
