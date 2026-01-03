package com.joojoo.api.block.application;

import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;

public interface BlockService {

    void deleteBlock(Long userId, Long blockId, DeleteMode mode);

    void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto);
}
