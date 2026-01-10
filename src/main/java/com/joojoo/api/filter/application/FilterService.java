package com.joojoo.api.filter.application;

import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;

public interface FilterService {

    RelatedKeywordsResponse getFilterRelation(Long userId, TickerAndTagIdDto tickerAndTagIdDto);
}
