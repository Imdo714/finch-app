package com.joojoo.api.tradeLog.application.service.command;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.api.tradeLog.application.port.in.DeleteTradeLogUseCase;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteTradeLogService implements DeleteTradeLogUseCase {

    private final TradeLogRepository tradeLogRepository;
    private final BlockTagRepository blockTagRepository;
    private final MetadataPort metadataPort;

    @Override
    @Transactional
    public void deleteTradeLog(Long userId, Long tradeLogId) {
        TradeLog tradeLog = tradeLogRepository.getTradeLogById(tradeLogId);
        tradeLog.validateOwner(userId);

        // Redis 삭제용
        List<BlockTag> tagsToRemove = blockTagRepository.findAllTagsByTradeLogIds(Collections.singletonList(tradeLogId));

        // 연관 테이블 일괄 삭제
        tradeLogRepository.clearMetadataByTradeLog(userId, tradeLogId);

        if (!tagsToRemove.isEmpty()) {
            metadataPort.processRedisTagRemoval(userId, tagsToRemove);
        }
    }

}
