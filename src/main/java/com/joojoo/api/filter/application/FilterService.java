package com.joojoo.api.filter.application;

import com.joojoo.api.filter.presentation.dto.request.FilterListDto;
import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;

public interface FilterService {

    FilterCountResponse getFilterCategoryCount(Long userId, FilterListDto filterListDto);

    RelatedKeywordsResponse getFilterRelation(Long userId, TickerAndTagIdDto tickerAndTagIdDto);
}
