package com.joojoo.api.tradeLog.application;

import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.api.util.metadata.MetadataService;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeLogServiceImpl implements TradeLogService {

    private final TradeLogRepository tradeLogRepository;
    private final UserRepository userRepository;
    private final TickerRepository tickerRepository;
    private final MetadataService metadataService;

    @Override
    @Transactional
    public void createTradesLog(Long userId, TradeRequestDto tradeRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Ticker ticker = tickerRepository.findById(tradeRequestDto.getTickerId())
                .orElseThrow(TickerNotFoundException::new);

        TradeLog tradeLog = TradeLog.create(user, ticker, tradeRequestDto);

        TradeLog save = tradeLogRepository.save(tradeLog);
//        metadataService.processTradeLogMetadata(save, userId);
    }

}
