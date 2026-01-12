package com.joojoo.api.block.application.port.in;

import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;

import java.time.LocalDate;

public interface GetBlockUseCase {
    DailyBlockDetailsResponse getBlockMainView(Long userId, LocalDate lastDate);

    BlockDetailResponseDto getBlockDetail(Long blockId);
}
