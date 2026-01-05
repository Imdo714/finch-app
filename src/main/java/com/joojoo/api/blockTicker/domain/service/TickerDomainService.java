package com.joojoo.api.blockTicker.domain.service;

import com.joojoo.api.ticker.presentation.dto.request.range.TickerSearchRange;
import com.joojoo.api.util.port.in.JasoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TickerDomainService {

    private final JasoConverter jasoConverter;

    public TickerSearchRange createSearchRange(String query) {
        String converted = jasoConverter.convert(query);
        return new TickerSearchRange(converted, converted + "\uffff");
    }
}
