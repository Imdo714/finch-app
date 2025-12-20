package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;

public interface BlockQueryDslRepository {
    List<Block> findAllChildrenByRootId(Long blockId);

}
