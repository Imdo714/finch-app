package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.presentation.dto.response.RecentTagsResponse;

public interface BlockTagService {
    RecentTagsResponse getRecentTags(Long userId);
}
