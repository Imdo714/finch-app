package com.joojoo.api.tradeLog.application.service.command;

import com.joojoo.api.tradeLog.application.port.in.UpdateTradeLogUseCase;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.tradeLog.presentation.dto.request.update.UpdateTradeRequestDto;
import com.joojoo.global.exception.handleException.tradeLog.NotTradeLogOwnerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTradeLogService implements UpdateTradeLogUseCase {

    private final TradeLogRepository tradeLogRepository;

    @Override
    @Transactional
    public void updateTradeLog(Long userId, UpdateTradeRequestDto updateTradeRequestDto) {
        TradeLog tradeLog = tradeLogRepository.findById(updateTradeRequestDto.getTradeLogId());

        if (!tradeLog.getUser().getId().equals(userId)) {
            throw new NotTradeLogOwnerException();
        }

        tradeLog.update(updateTradeRequestDto);
    }

}
