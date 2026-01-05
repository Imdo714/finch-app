package com.joojoo.api.tradeLog.application.service.command;

import com.joojoo.api.metadata.application.port.in.MetadataUseCase;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.tradeLog.application.port.in.CreateTradeLogUseCase;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateTradeLogService implements CreateTradeLogUseCase {

    private final TradeLogRepository tradeLogRepository;
    private final UserRepository userRepository;
    private final TickerRepository tickerRepository;
    private final MetadataUseCase metadataUseCase;


    @Override
    @Transactional
    public void createTradesLog(Long userId, TradeRequestDto tradeRequestDto) {
        User user = userRepository.getReferenceById(userId);
        Ticker ticker = tickerRepository.getReferenceById(tradeRequestDto.getTickerId());

        TradeLog tradeLog = TradeLog.create(user, ticker, tradeRequestDto);
        TradeLog save = tradeLogRepository.save(tradeLog);

        metadataUseCase.processTradeLogMetadata(save, userId, ticker);
    }
}
