package com.joojoo.api.search.application;

import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;

public interface SearchService {

    void recordSearch(Long userId, SearchRequestDto requestDto);

    RecentSearchListResponse recordSearchList(Long userId);
}
