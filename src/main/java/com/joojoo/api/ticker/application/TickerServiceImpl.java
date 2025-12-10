package com.joojoo.api.ticker.application;

import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.global.exception.handleException.tickers.InvalidTickerOrNameException;
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {

    private final TickerRedisRepository tickerRedisRepository;

    @Override
    public void addStockToRedis(String name, String ticker) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(ticker)) {
            throw new InvalidTickerOrNameException();
        }
        Set<String> tickers = new HashSet<>();
        tickers.add(HangulUtils.splitToJaso(name) + "*" + name + "*" + ticker);
        tickers.add(HangulUtils.getChosung(name) + "*" + name + "*" + ticker);

        tickerRedisRepository.addStocksToRedis(tickers);
    }

    @Override
    public TickerSearchResponse search(String query) {
        if (!StringUtils.hasText(query)) return TickerSearchResponse.of(Collections.emptySet());
        String convertedQuery = HangulUtils.splitToJaso(query); // query를 자모로 분해 해서 검색

        // Range.rightOpen는 [start, end]를 의미함 convertedQuery으로 시작하는 글자와 convertedQuery뒤에 붙는 글자를 찾는다
        Range<String> range = Range.rightOpen(convertedQuery, convertedQuery + "\uffff");
        return TickerSearchResponse.of(tickerRedisRepository.searchTickerQuery(range, Limit.limit().count(10)));
    }
}
