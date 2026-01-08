package com.joojoo.api.util.filter.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.application.test.FilterQuery;
import com.joojoo.api.blockTag.domain.service.assembler.BlockTagAssembler;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.util.filter.dto.request.RelatedKeywordsDto;
import com.joojoo.api.util.filter.dto.request.TagListDto;
import com.joojoo.api.util.filter.dto.request.TickerAndTagIdDto;
import com.joojoo.api.util.filter.dto.response.FilterCountResponse;
import com.joojoo.api.util.filter.dto.response.RelatedKeywordsResponse;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.assembler.detailApiAssembler.DetailResponseAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final FilterQuery filterQuery;
    private final BlockTreeValidator blockTreeValidator;
    private final BlockTagAssembler blockTagAssembler;

    private final DetailResponseAssembler detailResponseAssembler;

    @Override
    public DailyBlockDetailsResponse getFilterCategory(Long userId, TagListDto tagListDto, LocalDate lastDate) {
        tagListDto.validateHasKeywords();

        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        FilterCategory category = tagListDto.getCategory();

        List<LocalDate> targetDates = filterQuery.getTargetDates(userId, tagListDto.getTagIds(), tagListDto.getTickerIds(), targetDate, category, 3);

        if (targetDates.isEmpty()) {
            return DailyBlockDetailsResponse.of(Collections.emptyList(), false, null);
        }
        List<LocalDate> displayDates = targetDates.stream().limit(2).toList();

        List<Block> blocks = Collections.emptyList();
        List<TradeLog> tradeLogs = Collections.emptyList();

        if (category == FilterCategory.ALL || category == FilterCategory.BLOCK) {
            blocks = filterQuery.searchBlocksWithAllKeywords(tagListDto.getTagIds(), tagListDto.getTickerIds(), userId, displayDates);
        }

        if (category == FilterCategory.ALL || category == FilterCategory.BUY || category == FilterCategory.SELL) {
            tradeLogs = filterQuery.searchTradeLogsWithAllKeywords(tagListDto.getTagIds(), tagListDto.getTickerIds(), userId, displayDates, category);
        }

        return detailResponseAssembler.assembleTagsAndTickersDetailResponse(targetDates, blocks, tradeLogs);
    }

    @Override
    public FilterCountResponse getFilterCategoryCount(Long userId, TagListDto tagListDto) {
        tagListDto.validateHasKeywords();

        FilterCategory category = tagListDto.getCategory();
        List<Long> tagIds = tagListDto.getTagIds();
        List<Long> tickerIds = tagListDto.getTickerIds();

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
