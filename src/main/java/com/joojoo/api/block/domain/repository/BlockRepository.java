package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;

public interface BlockRepository {
    Block save(Block block);
}
