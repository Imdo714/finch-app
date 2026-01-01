package com.joojoo.api.tradeLog.application;

import com.joojoo.api.tradeLog.application.port.in.DeleteTradeLogUseCase;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeLogUseCaseService implements DeleteTradeLogUseCase {

    private final TradeLogRepository tradeLogRepository;

    @Override
    public void withdrawByUserId(Long userId) {
        tradeLogRepository.withdrawByUserId(userId);
    }
}
