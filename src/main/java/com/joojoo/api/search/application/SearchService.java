package com.joojoo.api.search.application;

import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;

public interface SearchService {

    TagHistoryResponseDto searchTags(Long userId, String query);

    void recordSearch(Long userId, SearchRequestDto requestDto);

    RecentSearchListResponse recordSearchList(Long userId);
}
