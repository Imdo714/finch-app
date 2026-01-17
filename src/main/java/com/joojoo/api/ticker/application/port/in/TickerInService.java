package com.joojoo.api.ticker.application.port.in;

import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.Map;
import java.util.Set;

public interface TickerInService {
    Map<String, Ticker> getTickerMap(Set<String> tickerNames);
}
