package com.joojoo.api.blockTag.application.port.in;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.time.LocalDate;

public interface GetBlockTagUseCase {
    RecentTagsResponse getRecentTags(Long userId);

    BlockTagsResponse getBlockTagDetail(Long userId, Long tagId, LocalDate lastDate);
}
