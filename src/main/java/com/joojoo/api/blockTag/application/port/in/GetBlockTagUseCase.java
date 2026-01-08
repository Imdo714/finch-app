package com.joojoo.api.blockTag.application.port.in;

import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.common.domain.response.detail.count.TotalCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.time.LocalDate;

public interface GetBlockTagUseCase {
    RecentTagsResponse getRecentTags(Long userId);

    DailyBlockDetailsResponse getBlockTagDetail(Long userId, Long tagId, LocalDate lastDate);

    TotalCountResponse getBlockCount(Long userId, Long tagId);
}
