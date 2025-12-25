package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

public interface BlockTagService {
    RecentTagsResponse getRecentTags(Long userId);

    BlockTagsResponse getUserTagIdsByTagId(Long userId, Long tagId, Long lastBlockId, int pageSize);

    BlockTagCountResponse getBlockCount(Long userId, Long tagId);
}
