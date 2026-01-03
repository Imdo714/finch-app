package com.joojoo.api.block.application.port.in;

import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;

public interface UpdateBlockUseCase {
    void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto);
}
