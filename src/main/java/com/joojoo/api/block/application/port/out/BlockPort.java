package com.joojoo.api.block.application.port.out;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;

/** CRUD만 사용 */
public interface BlockPort {
    void saveAll(List<Block> allBlocks);
}
