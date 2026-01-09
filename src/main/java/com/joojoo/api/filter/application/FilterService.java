package com.joojoo.api.filter.application;

import com.joojoo.api.filter.presentation.dto.request.TagListDto;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;

import java.time.LocalDate;

public interface FilterService {
    DailyBlockDetailsResponse getFilterCategory(Long userId, TagListDto tagListDto, LocalDate lastDate);

    FilterCountResponse getFilterCategoryCount(Long userId, TagListDto tagListDto);

    RelatedKeywordsResponse getFilterRelation(Long userId, TickerAndTagIdDto tickerAndTagIdDto);
}
