package com.joojoo.api.filter.application.service.query;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.common.assembler.detailApiAssembler.DetailResponseAssembler;
import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.filter.application.port.in.GetFilterUseCase;
import com.joojoo.api.filter.application.port.out.BlockAndTradeFilterDatePort;
import com.joojoo.api.filter.application.port.out.FilterQueryPort;
import com.joojoo.api.filter.presentation.dto.request.FilterListDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilterQueryService implements GetFilterUseCase {

    private final BlockTreeValidator blockTreeValidator;
    private final DetailResponseAssembler detailResponseAssembler;
    private final BlockAndTradeFilterDatePort blockAndTradeFilterDatePort;
    private final FilterQueryPort filterQueryPort;

    @Override
    public DailyBlockDetailsResponse getFilterCategory(Long userId, FilterListDto filterListDto, LocalDate lastDate) {
        filterListDto.validateHasKeywords();
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        FilterCategory category = filterListDto.getCategory();

        List<LocalDate> targetDates = blockAndTradeFilterDatePort.findTargetDates(userId, filterListDto.getTagIds(), filterListDto.getTickerIds(), targetDate, filterListDto.getCategory(), 3);

        if (targetDates.isEmpty()) {
            return DailyBlockDetailsResponse.of(Collections.emptyList(), false, null);
        }

        List<LocalDate> displayDates = targetDates.stream().limit(2).toList();

        List<Block> blocks = category.isBlockApplicable() ?
                filterQueryPort.findBlocksByCriteria(filterListDto.getTagIds(), filterListDto.getTickerIds(), userId, displayDates) : Collections.emptyList();

        List<TradeLog> tradeLogs = category.isTradeLogApplicable() ?
                filterQueryPort.findTradeLogsByCriteria(filterListDto.getTagIds(), filterListDto.getTickerIds(), userId, displayDates, category) : Collections.emptyList();

        return detailResponseAssembler.assembleTagsAndTickersDetailResponse(targetDates, blocks, tradeLogs);
    }

    @Override
    public FilterCountResponse getFilterCategoryCount(Long userId, FilterListDto dto) {
        dto.validateHasKeywords();
        FilterCategory category = dto.getCategory();

        long blockCount = category.isBlockApplicable() ?
                filterQueryPort.countBlocksByCriteria(dto.getTagIds(), dto.getTickerIds(), userId) : 0;

        long tradeLogsCount = category.isTradeLogApplicable() ?
                filterQueryPort.countTradeLogsByCriteria(dto.getTagIds(), dto.getTickerIds(), userId, category) : 0;

        return new FilterCountResponse(blockCount + tradeLogsCount);
    }

}
