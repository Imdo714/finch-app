package com.joojoo.api.ticker.application.port.in;

import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

public interface GetTickerUseCase {
    TickerSearchResponse search(String query);
}
