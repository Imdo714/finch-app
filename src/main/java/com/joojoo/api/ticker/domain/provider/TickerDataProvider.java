package com.joojoo.api.ticker.domain.provider;

import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;

import java.util.List;

public interface TickerDataProvider {
    List<TickerDataDto> getTickerCsvData();
}
