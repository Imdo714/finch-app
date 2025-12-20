package com.joojoo.api.blockTicker.domain.repository;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;

import java.util.List;

public interface BlockTickerRepository {
    List<BlockTicker> saveAll(List<BlockTicker> blockTickers);
}
