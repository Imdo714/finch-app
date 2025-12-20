package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;

public interface BlockRepository {
    Block save(Block block);

    List<Block> saveAll(List<Block> allBlocks);

    List<Block> findAllChildrenByRootId(Long blockId);
}
