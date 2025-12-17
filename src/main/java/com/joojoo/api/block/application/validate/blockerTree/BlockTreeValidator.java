package com.joojoo.api.block.application.validate.blockerTree;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;

public interface BlockTreeValidator {
    void validateStructure(BlockSaveRequestDto requestDto);
}
