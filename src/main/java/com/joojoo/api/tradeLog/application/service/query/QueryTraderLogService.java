package com.joojoo.api.tradeLog.application.service.query;

import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.tradeLog.application.port.in.GetTraderLogUseCase;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.domain.service.calculate.TradeMetrics;
import com.joojoo.api.tradeLog.presentation.dto.request.calculate.TradeCalculateRequest;
import com.joojoo.api.tradeLog.presentation.dto.response.calculate.TradeMetricsResponse;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryTraderLogService implements GetTraderLogUseCase {

    private final TradeLogRepository tradeLogRepository;
    private final TickerRepository tickerRepository;
    private final BlockRepository blockRepository;

    @Override
    public TradeMetricsResponse getTradeLogCalculate(Long userId, TradeCalculateRequest tradeCalculateRequest) {
        if(!tickerRepository.existsById(tradeCalculateRequest.getTickerId())){
            throw new TickerNotFoundException();
        }

        TradeMetrics metrics = tradeLogRepository.findAllBuyLogsByTicker(userId, tradeCalculateRequest.getTickerId());
        if (metrics == null || metrics.getCurrentQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            return TradeMetricsResponse.empty();
        }

        return metrics.calculate(tradeCalculateRequest.getCurrentPrice());
    }

    @Override
    public ChartOverlayResponse getTradeLogChart(Long userId, Long tickerId) {
        if(!tickerRepository.existsById(tickerId)){
            throw new TickerNotFoundException();
        }

        List<ChartOverlayResponse.TradeDetailDto> logs = tradeLogRepository.getTradeBuySellRecords(userId, tickerId);
        List<ChartOverlayResponse.AnalysisBlockDto> blocks = blockRepository.findByUserIdAndTickerId(userId, tickerId);

        return ChartOverlayResponse.of(logs, blocks);
    }

}
