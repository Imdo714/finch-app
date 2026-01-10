package com.joojoo.api.filter.application.port.in;

import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.filter.presentation.dto.request.FilterListDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;

import java.time.LocalDate;

public interface GetFilterUseCase {
    DailyBlockDetailsResponse getFilterCategory(Long userId, FilterListDto filterListDto, LocalDate lastDate);

    FilterCountResponse getFilterCategoryCount(Long userId, FilterListDto filterListDto);
}
