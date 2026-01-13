package com.joojoo.api.tradeLog.application.service.query;

import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.tradeLog.application.port.in.GetTraderLogUseCase;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.presentation.dto.request.calculate.TradeCalculateRequest;
import com.joojoo.api.tradeLog.domain.service.TradeMetrics;
import com.joojoo.api.tradeLog.presentation.dto.response.calculate.TradeMetricsResponse;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class QueryTraderLogService implements GetTraderLogUseCase {

    private final TradeLogRepository tradeLogRepository;
    private final TickerRepository tickerRepository;

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

}
