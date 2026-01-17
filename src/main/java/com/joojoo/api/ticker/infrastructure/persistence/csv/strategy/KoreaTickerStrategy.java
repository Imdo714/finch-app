package com.joojoo.api.ticker.infrastructure.persistence.csv.strategy;

import com.joojoo.api.ticker.application.port.out.TickerMappingStrategy;
import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KoreaTickerStrategy implements TickerMappingStrategy {

    @Override
    public Optional<TickerDataDto> TickerConverter(String[] row) {
        return TickerDataDto.fromKoreaCsv(row);
    }
}
