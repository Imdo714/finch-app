package com.joojoo.api.ticker.application;

import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.global.exception.handleException.tickers.InvalidTickerOrNameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {

    private final TickerRedisRepository tickerRedisRepository;
    private final HangulConverter hangulConverter;

    @Override
    public void addStockToRedis(String name, String ticker) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(ticker)) {
            throw new InvalidTickerOrNameException();
        }
        Set<String> tickers = new HashSet<>();
        tickers.add(hangulConverter.jasoConvert(name) + "*" + name + "*" + ticker);
        tickers.add(hangulConverter.chosungConvert(name) + "*" + name + "*" + ticker);

        tickerRedisRepository.addStocksToRedis(tickers);
    }

}
