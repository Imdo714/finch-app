package com.joojoo.api.blockTicker.infrastructure.queryDsl;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.response.recent.RecentTickersResponse;

import java.util.List;

public interface BlockTickerQueryDslRepository {
    List<BlockTicker> findAllBlockTickers(List<Long> blockIds);

    List<BlockTicker> findAllTickersByTradeLogIds(List<Long> tradeLogIds);

    List<BlockTicker> findAllTickersByBlockId(Long blockId);

    List<BlockTicker> findAllTickersByBlockIdIn(List<Long> idsToDelete);

    List<RecentTickersResponse.RecentTickersDto> findRecentTickers(Long userId);

    void deleteByTradeLogId(Long tradeLogId);
}
