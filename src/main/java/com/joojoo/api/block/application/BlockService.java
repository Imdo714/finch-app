package com.joojoo.api.block.application;

import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;

import java.time.LocalDate;

public interface BlockService {
    BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto);

    BlockDetailResponseDto getBlockDetail(Long blockId);

    BlockMainViewResponse getBlockMainView(Long userId, LocalDate lastDate);

    void deleteBlock(Long userId, Long blockId, DeleteMode mode);
}
