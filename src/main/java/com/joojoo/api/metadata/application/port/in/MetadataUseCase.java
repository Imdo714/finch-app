package com.joojoo.api.metadata.application.port.in;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.util.List;

public interface MetadataUseCase {
    /** List<Block> 타입으로 티커, 태그 찾기 */
    void processMetadata(List<Block> allBlocks, Long userId);

    /** Block 단일 타입으로 티커, 태그 찾기 (수정용) */
    void processMetadata(Block targetBlock, Long userId);

    /** TradeLog 타입으로 티커, 태그 찾기 */
    void processTradeLogMetadata(TradeLog save, Long userId, Ticker ticker);
}
