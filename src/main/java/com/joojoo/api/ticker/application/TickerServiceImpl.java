package com.joojoo.api.ticker.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.provider.TickerDataProvider;
import com.joojoo.api.ticker.domain.repository.TickerRedisRepository;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.util.detailQuery.BlockTradeLogQueryService;
import com.joojoo.global.exception.handleException.tickers.InvalidTickerOrNameException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TickerServiceImpl implements TickerService {

    private final TickerRedisRepository tickerRedisRepository;
    private final TickerRepository tickerRepository;
    private final TickerDataProvider tickerDataProvider;
    private final UserRepository userRepository;
    private final BlockTradeLogQueryService blockTradeLogQueryService;
    private final BlockTreeValidator blockTreeValidator;
    private final BlockTickerRepository blockTickerRepository;

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

    @Override
    @Transactional
    public void initTickerData() {
        List<TickerDataDto> externalStocks = tickerDataProvider.getTickerCsvData();

        // 과연 전체 주식을 다 가져오는게 효율적일 까??
        Map<String, Ticker> existingTickerMap = tickerRepository.findAll().stream()
                .collect(Collectors.toMap(Ticker::getSymbol, ticker -> ticker));

        List<Ticker> saveList = new ArrayList<>();

        for (TickerDataDto data : externalStocks) {
            Ticker ticker = existingTickerMap.get(data.getSymbol());

            if (ticker != null) {
                ticker.updateInfo(data.getName(), data.getMarket(), data.getListingDate());
                saveList.add(ticker);
            } else {
                saveList.add(new Ticker(data.getSymbol(), data.getName(), data.getMarket(), data.getListingDate()));
            }
        }

        tickerRepository.saveAll(saveList);
        log.info("티커 데이터 업데이트 완료: {}건 처리됨", saveList.size());
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

    @Override
    public BlockTagsResponse getTickerList(Long userId, Long tickerId, LocalDate lastDate) {
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        TickerDateResult result = blockTickerRepository.findAllByTickerAndDate(userId, tickerId, targetDate);

        if (result.getContent().isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
        }

        List<LocalDate> displayDates = result.getTargetDates().stream().limit(2).toList();

        // 필터링된 Block과 TradeLog 추출
        List<Block> blocks = result.getContent().stream()
                .filter(bt -> displayDates.contains(getCreatedAt(bt).toLocalDate()))
                .map(BlockTicker::getBlock)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<TradeLog> tradeLogs = result.getContent().stream()
                .filter(bt -> displayDates.contains(getCreatedAt(bt).toLocalDate()))
                .map(BlockTicker::getTradeLog)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return blockTradeLogQueryService.assembleBlockTagsResponse(result.getTargetDates(), blocks, tradeLogs);
    }

    private LocalDateTime getCreatedAt(BlockTicker bt) {
        return (bt.getBlock() != null) ? bt.getBlock().getCreatedAt() : bt.getTradeLog().getCreatedAt();
    }

    private Stream<String> generateSearchKeywords(Ticker stock) {
        String name = stock.getName();
        String symbol = stock.getSymbol();

        return Stream.of(
                HangulUtils.splitToJaso(name) + "*" + name + "*" + symbol,
                HangulUtils.getChosung(name) + "*" + name + "*" + symbol
        );
    }

}
