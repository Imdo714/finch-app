package com.joojoo.api.block.application;

import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;

public interface BlockService {
    BlockDetailResponseDto getBlockDetail(Long blockId);

    void deleteBlock(Long userId, Long blockId, DeleteMode mode);

    void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto);
}
