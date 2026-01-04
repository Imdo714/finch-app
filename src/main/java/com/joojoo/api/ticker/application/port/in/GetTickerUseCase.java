package com.joojoo.api.ticker.application.port.in;

import com.joojoo.api.blockTag.presentation.dto.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

import java.time.LocalDate;

public interface GetTickerUseCase {
    TickerSearchResponse search(String query);

    DailyBlockDetailsResponse getTickerDetail(Long userId, Long tickerId, LocalDate lastDate);
}
