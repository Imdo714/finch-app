package com.joojoo.api.block.application;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;

public interface BlockService {
    BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto);
}
