package com.joojoo.api.filter.application;

import com.joojoo.api.filter.infrastructure.test.FilterQuery;
import com.joojoo.api.filter.presentation.dto.request.RelatedKeywordsDto;
import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final FilterQuery filterQuery;

    @Override
    public RelatedKeywordsResponse getFilterRelation(Long userId, TickerAndTagIdDto dto) {
        dto.validate();

        RelatedKeywordsDto data = dto.isTagSearch()
                ? filterQuery.findRelatedKeywordsByTag(userId, dto.getTagId())
                : filterQuery.findRelatedKeywordsByTicker(userId, dto.getTickerId());

        return RelatedKeywordsResponse.of(data.getRelatedTags(), data.getRelatedTickers());
    }

}
