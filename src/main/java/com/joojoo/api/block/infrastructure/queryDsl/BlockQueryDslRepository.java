package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;
import java.util.Map;

public interface BlockQueryDslRepository {
    List<Block> findAllChildrenByRootId(Long blockId);

    List<Block> findRootBlocks(Long userId, Long lastId, int size);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    List<Block> getBlocksByTagId(Long userId, Long tagId, Long lastBlockId, int limit);
}
