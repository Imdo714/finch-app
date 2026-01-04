package com.joojoo.api.ticker.presentation.dto.request.range;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TickerSearchRange {
    private final String start;
    private final String end;
}
