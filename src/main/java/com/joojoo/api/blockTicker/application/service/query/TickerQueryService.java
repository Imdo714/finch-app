package com.joojoo.api.blockTicker.application.service.query;

import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.blockTicker.application.port.in.GetTickerUseCase;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.domain.service.BlockTickerAssembler;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import com.joojoo.api.blockTicker.presentation.dto.response.recent.RecentTickersResponse;
import com.joojoo.api.common.date.DateUtils;
import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.common.domain.response.detail.count.TotalCountResponse;
import com.joojoo.api.common.search.SearchRangeFactory;
import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TickerQueryService implements GetTickerUseCase {

    private final TickerRedisRepository tickerRedisRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final TickerRepository tickerRepository;
    private final DateUtils dateUtils;
    private final BlockTickerAssembler blockTickerAssembler;
    private final SearchRangeFactory searchRangeFactory;

    @Override
    public TickerSearchResponse search(String query) {
        if (!StringUtils.hasText(query)) return TickerSearchResponse.of(Collections.emptySet());

        SearchRange range = searchRangeFactory.createSearchRange(query);
        return TickerSearchResponse.of(tickerRedisRepository.searchTickerQuery(range, 10));
    }

    @Override
    public DailyBlockDetailsResponse getTickerDetail(Long userId, Long tickerId, LocalDate lastDate) {
        LocalDate targetDate = dateUtils.validateAndGetTargetDate(lastDate);
        TickerDateResult result = blockTickerRepository.findAllByTickerAndDate(userId, tickerId, targetDate);

        if (result.getContent().isEmpty()) {
            return DailyBlockDetailsResponse.of(Collections.emptyList(), false, null);
        }

        List<LocalDate> displayDates = result.getTargetDates().stream().limit(2).toList();
        return blockTickerAssembler.assembleBlockTickersResponse(result, displayDates);
    }

    @Override
    public TotalCountResponse getTickerDetailCount(Long userId, Long tickerId) {
        RelatedBlockDetailCountResponse tickerCount = tickerRepository.getTickerDetailCount(userId, tickerId);

        if (tickerCount == null) {
            throw new TickerNotFoundException();
        }
        return new TotalCountResponse(tickerCount.getName(), tickerCount.getTotalCount());
    }

    @Override
    public RecentTickersResponse getRecentTickers(Long userId) {
        return RecentTickersResponse.of(blockTickerRepository.findRecentTickers(userId));
    }

}
