package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;
import java.util.Map;

public interface BlockQueryDslRepository {
    List<Block> findAllChildrenByRootId(Long blockId);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    void deleteAllBlockMappings(Long userId);
}
