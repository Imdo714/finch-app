package com.joojoo.api.search.application;

import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;

public interface SearchService {

    RecentSearchListResponse recordSearchList(Long userId);
}
