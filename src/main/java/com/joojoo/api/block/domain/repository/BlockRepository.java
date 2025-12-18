package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;

public interface BlockRepository {
    Block save(Block block);

    void saveAll(List<Block> allBlocks);
}
