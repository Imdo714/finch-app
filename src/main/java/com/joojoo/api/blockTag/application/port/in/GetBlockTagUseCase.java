package com.joojoo.api.blockTag.application.port.in;

import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

public interface GetBlockTagUseCase {
    RecentTagsResponse getRecentTags(Long userId);
}
