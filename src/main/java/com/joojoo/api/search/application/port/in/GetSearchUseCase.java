package com.joojoo.api.search.application.port.in;

import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import com.joojoo.api.search.presentation.dto.response.ticker.TickerHistoryResponseDto;

public interface GetSearchUseCase {
    TagHistoryResponseDto searchTags(Long userId, String query);

    /** 사용자가 사용한 티커 조회 고도화 때 사용할 예정 */
    TickerHistoryResponseDto searchTickers(Long userId, String query);

    RecentSearchListResponse recordSearchList(Long userId);
}
