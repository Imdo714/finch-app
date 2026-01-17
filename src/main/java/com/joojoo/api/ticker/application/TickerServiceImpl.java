package com.joojoo.api.ticker.application;

import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.tickers.InvalidTickerOrNameException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {

    private final TickerRedisRepository tickerRedisRepository;
    private final TickerRepository tickerRepository;
    private final UserRepository userRepository;
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

    @Override
    public void loadTickersToCache(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.validateAdminPermission();

        List<Ticker> allStocks = tickerRepository.findAll();
        Set<String> tickers = allStocks.stream()
                .filter(ticker -> StringUtils.hasText(ticker.getName()) && StringUtils.hasText(ticker.getSymbol()))
                .flatMap(this::generateSearchKeywords)
                .collect(Collectors.toSet());

        if (!tickers.isEmpty()) {
            tickerRedisRepository.addStocksToRedis(tickers);
        }
        log.info("티커 Cache 업데이트 완료: {}건 처리됨", tickers.size());
    }

    private Stream<String> generateSearchKeywords(Ticker stock) {
        String name = stock.getName();
        String symbol = stock.getSymbol();
        Long tickerId = stock.getId();

        return Stream.of(
                hangulConverter.jasoConvert(name) + "*" + name + "*" + symbol + "*" + tickerId,
                hangulConverter.chosungConvert(name) + "*" + name + "*" + symbol + "*" + tickerId
        );
    }

}
