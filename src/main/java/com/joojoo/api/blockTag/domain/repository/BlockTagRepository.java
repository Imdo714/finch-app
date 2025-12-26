package com.joojoo.api.blockTag.domain.repository;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.util.List;

public interface BlockTagRepository {
    List<BlockTag> saveAll(List<BlockTag> blockTags);

    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    BlockTagCountResponse getBlockCount(Long userId, Long tagId);

    void deleteByBlockIds(Long id);
}
