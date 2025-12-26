package com.joojoo.api.blockTicker.domain.repository;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;

import java.util.List;

public interface BlockTickerRepository {
    List<BlockTicker> saveAll(List<BlockTicker> blockTickers);

    List<BlockTicker> findAllBlockTickers(List<Long> blockIds);

    void deleteByBlockIds(Long id);
}
