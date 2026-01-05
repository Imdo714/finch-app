package com.joojoo.api.blockTicker.application.port.in;

import com.joojoo.global.common.response.detail.DailyBlockDetailsResponse;
import com.joojoo.global.common.response.detail.count.TotalCountResponse;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;

import java.time.LocalDate;

public interface GetTickerUseCase {
    TickerSearchResponse search(String query);

    DailyBlockDetailsResponse getTickerDetail(Long userId, Long tickerId, LocalDate lastDate);

    TotalCountResponse getTickerDetailCount(Long userId, Long tickerId);
}
