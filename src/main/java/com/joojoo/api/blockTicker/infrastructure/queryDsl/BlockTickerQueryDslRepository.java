package com.joojoo.api.blockTicker.infrastructure.queryDsl;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;

import java.util.List;

public interface BlockTickerQueryDslRepository {
    List<BlockTicker> findAllBlockTickers(List<Long> blockIds);
}
