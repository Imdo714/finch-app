package com.joojoo.api.blockTag.domain.repository;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;

import java.util.List;

public interface BlockTagRepository {
    List<BlockTag> saveAll(List<BlockTag> blockTags);
}
