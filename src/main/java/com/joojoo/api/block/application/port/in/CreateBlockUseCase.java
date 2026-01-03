package com.joojoo.api.block.application.port.in;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;

public interface CreateBlockUseCase {
    /** 블럭 생성 */
    BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto);
}
