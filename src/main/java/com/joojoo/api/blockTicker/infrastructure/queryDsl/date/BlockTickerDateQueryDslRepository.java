package com.joojoo.api.blockTicker.infrastructure.queryDsl.date;

import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;

import java.time.LocalDate;

public interface BlockTickerDateQueryDslRepository {
    TickerDateResult findAllByTickerAndDate(Long userId, Long tickerId, LocalDate targetDate);
}
