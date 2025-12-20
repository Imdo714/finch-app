package com.joojoo.api.blockTag.infrastructure.queryDsl;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;

import java.util.List;

public interface BlockTagQueryDslRepository {
    List<BlockTag> findAllBlockTags(List<Long> blockIds);
}
