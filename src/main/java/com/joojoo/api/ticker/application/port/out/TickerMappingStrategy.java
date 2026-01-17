package com.joojoo.api.ticker.application.port.out;

import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;

import java.util.Optional;

public interface TickerMappingStrategy {
    Optional<TickerDataDto> TickerConverter(String[] row);
}
