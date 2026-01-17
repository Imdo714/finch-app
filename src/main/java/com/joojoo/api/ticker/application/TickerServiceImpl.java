package com.joojoo.api.ticker.application;

import com.joojoo.api.common.hangul.HangulConverter;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.domain.service.SearchKeywordMapper;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.tickers.InvalidTickerOrNameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {

    private final TickerRedisRepository tickerRedisRepository;
    private final TickerRepository tickerRepository;
    private final UserRepository userRepository;
    private final HangulConverter hangulConverter;
    private final SearchKeywordMapper searchKeywordMapper;

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
    @Transactional(readOnly = true)
    public void loadTickersToCache(Long userId) {
        User user = userRepository.getUserById(userId);
        user.validateAdminPermission();

        List<MarketType> targetMarkets = MarketType.getActiveMarkets();
        for (MarketType market : targetMarkets) {
            // 시장 별 데이터 조회
            List<Ticker> stocks = tickerRepository.findByTickerMarket(market);

            Set<String> searchIndexes = stocks.stream()
                    .filter(s -> StringUtils.hasText(s.getName()) && StringUtils.hasText(s.getSymbol()))
                    .flatMap(searchKeywordMapper::mapToSearchIndex) // Redis에 저장할 내용 생성
                    .collect(Collectors.toSet());

            if (!searchIndexes.isEmpty()) {
                tickerRedisRepository.addStocksToRedis(searchIndexes);
            }
            log.info("{} 시장 캐시 업데이트 완료: {}건", market, searchIndexes.size());
        }
    }

}
