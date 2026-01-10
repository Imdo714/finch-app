package com.joojoo.api.filter.application;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.filter.infrastructure.test.FilterQuery;
import com.joojoo.api.filter.presentation.dto.request.RelatedKeywordsDto;
import com.joojoo.api.filter.presentation.dto.request.FilterListDto;
import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final FilterQuery filterQuery;

    @Override
    public FilterCountResponse getFilterCategoryCount(Long userId, FilterListDto filterListDto) {
        filterListDto.validateHasKeywords();

        FilterCategory category = filterListDto.getCategory();
        List<Long> tagIds = filterListDto.getTagIds();
        List<Long> tickerIds = filterListDto.getTickerIds();

        List<Block> blocks = new ArrayList<>();
        List<TradeLog> tradeLogs = new ArrayList<>();

        if (category == FilterCategory.ALL || category == FilterCategory.BLOCK) {
            blocks = filterQuery.searchBlocksWithAllKeywordsCount(tagIds, tickerIds, userId);
        }

        if (category == FilterCategory.ALL || category == FilterCategory.BUY || category == FilterCategory.SELL) {
            tradeLogs = filterQuery.searchTradeLogsWithAllKeywordsCount(tagIds, tickerIds, userId, category);
        }

        return new FilterCountResponse(blocks.size() + tradeLogs.size());
    }

    @Override
    public RelatedKeywordsResponse getFilterRelation(Long userId, TickerAndTagIdDto dto) {
        dto.validate();

        RelatedKeywordsDto data = dto.isTagSearch()
                ? filterQuery.findRelatedKeywordsByTag(userId, dto.getTagId())
                : filterQuery.findRelatedKeywordsByTicker(userId, dto.getTickerId());

        return RelatedKeywordsResponse.of(data.getRelatedTags(), data.getRelatedTickers());
    }

}
