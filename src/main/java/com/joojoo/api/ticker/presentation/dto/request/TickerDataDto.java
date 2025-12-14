package com.joojoo.api.ticker.presentation.dto.request;

import com.joojoo.api.ticker.domain.model.enums.MarketType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TickerDataDto {
    private String symbol;
    private String name;
    private MarketType market;
    private LocalDate listingDate;
}
