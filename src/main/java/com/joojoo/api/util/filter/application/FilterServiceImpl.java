package com.joojoo.api.util.filter.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.application.test.FilterQuery;
import com.joojoo.api.blockTag.presentation.dto.request.TagListDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.util.detailQuery.BlockTradeLogQueryService;
import com.joojoo.global.common.enums.FilterCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilterServiceImpl implements FilterService {

    private final FilterQuery filterQuery;
    private final BlockTreeValidator blockTreeValidator;
    private final BlockTradeLogQueryService blockTradeLogQueryService;

    @Override
    public BlockTagsResponse getFilterCategory(Long userId, TagListDto tagListDto, LocalDate lastDate) {
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        FilterCategory category = tagListDto.getCategory();

        List<LocalDate> targetDates = filterQuery.getTargetDates(userId, tagListDto.getTagIds(), tagListDto.getTickerIds(), targetDate, category, 3);

        if (targetDates.isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
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

        return blockTradeLogQueryService.assembleBlockTagsResponse(displayDates, blocks, tradeLogs);
    }
}
