package com.joojoo.api.ticker.application.in;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TickerInServiceImpl implements TickerInService {

    private final TickerRepository tickerRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Ticker> getTickerMap(Set<String> names) {
        if (names.isEmpty()) return Collections.emptyMap();
        return tickerRepository.findAllByTickerNames(names).stream()
                .collect(Collectors.toMap(Ticker::getName, t -> t));
    }
}
