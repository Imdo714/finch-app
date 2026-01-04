package com.joojoo.api.ticker.application.service.query;

import com.joojoo.api.blockTag.presentation.dto.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import com.joojoo.api.ticker.application.port.in.GetTickerUseCase;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.service.TickerDomainService;
import com.joojoo.api.ticker.domain.service.assembler.BlockTickerAssembler;
import com.joojoo.api.ticker.presentation.dto.request.range.TickerSearchRange;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.api.util.date.DateUtils;
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
    private final TickerDomainService tickerDomainService;
    private final BlockTickerRepository blockTickerRepository;
    private final DateUtils dateUtils;
    private final BlockTickerAssembler blockTickerAssembler;

    @Override
    public TickerSearchResponse search(String query) {
        if (!StringUtils.hasText(query)) return TickerSearchResponse.of(Collections.emptySet());

        TickerSearchRange range = tickerDomainService.createSearchRange(query);
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

}
