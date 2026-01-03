package com.joojoo.api.block.application;

import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;

import java.time.LocalDate;

public interface BlockService {
    BlockDetailResponseDto getBlockDetail(Long blockId);

    BlockMainViewResponse getBlockMainView(Long userId, LocalDate lastDate);

    void deleteBlock(Long userId, Long blockId, DeleteMode mode);

    void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto);
}
