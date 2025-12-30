package com.joojoo.api.search.application;

import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;

public interface SearchService {

    TagHistoryResponseDto searchTags(Long userId, String query);
}
