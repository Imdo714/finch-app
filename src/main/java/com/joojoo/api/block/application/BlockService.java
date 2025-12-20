package com.joojoo.api.block.application;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;

public interface BlockService {
    BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto);

    BlockDetailResponseDto getBlockDetail(Long blockId);
}
