package com.joojoo.api.ticker.application.service.command;

import com.joojoo.api.ticker.application.port.in.CreateTIckerUseCase;
import com.joojoo.api.ticker.application.port.out.TickerDataProvider;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.model.enums.MarketType;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.ticker.presentation.dto.request.TickerDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTickerService implements CreateTIckerUseCase {

    private final TickerRepository tickerRepository;
    private final TickerDataProvider tickerDataProvider;

    @Override
    @Transactional
    public void initKoreaTickerData() {
        List<TickerDataDto> externalStocks = tickerDataProvider.getTickerCsvData();
        List<Ticker> existingTickers = tickerRepository.findKoreaTickers(MarketType.koreaMarkets());

        processSynchronization(externalStocks, existingTickers);
    }

    @Override
    @Transactional
    public void initNasdaqTickerData() {
        List<TickerDataDto> externalStocks = tickerDataProvider.fetchNasdaqTickers();
        List<Ticker> existingTickers = tickerRepository.findByTickerMarket(MarketType.NASDAQ);

        processSynchronization(externalStocks, existingTickers);
    }

    @Override
    @Transactional
    public void initAmexTickerData() {
        List<TickerDataDto> externalStocks = tickerDataProvider.fetchAmexTickers();
        List<Ticker> existingTickers = tickerRepository.findByTickerMarket(MarketType.AMEX);

        processSynchronization(externalStocks, existingTickers);
    }

    @Override
    @Transactional
    public void initNyseTickerData() {
        List<TickerDataDto> externalStocks = tickerDataProvider.fetchNyseTickers();
        List<Ticker> existingTickers = tickerRepository.findByTickerMarket(MarketType.NYSE);

        processSynchronization(externalStocks, existingTickers);
    }

    private void processSynchronization(List<TickerDataDto> externalStocks, List<Ticker> currentTickers) {
        Map<String, Ticker> tickerMap = currentTickers.stream()
                .collect(Collectors.toMap(Ticker::getSymbol, t -> t));

        List<Ticker> toSave = TickerDataDto.getTickerList(externalStocks, tickerMap);
        tickerRepository.saveAll(toSave);
        log.info("티커 데이터 업데이트 완료: {}건 처리됨", toSave.size());
    }



}
