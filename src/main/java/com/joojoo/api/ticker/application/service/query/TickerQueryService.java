package com.joojoo.api.ticker.application.service.query;

import com.joojoo.api.ticker.application.port.in.GetTickerUseCase;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.service.TickerDomainService;
import com.joojoo.api.ticker.presentation.dto.request.range.TickerSearchRange;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TickerQueryService implements GetTickerUseCase {

    private final TickerRedisRepository tickerRedisRepository;
    private final TickerDomainService tickerDomainService;

    @Override
    public TickerSearchResponse search(String query) {
        if (!StringUtils.hasText(query)) return TickerSearchResponse.of(Collections.emptySet());

        TickerSearchRange range = tickerDomainService.createSearchRange(query);
        return TickerSearchResponse.of(tickerRedisRepository.searchTickerQuery(range, 10));
    }

}
