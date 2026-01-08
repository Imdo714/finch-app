package com.joojoo.api.search.application.port.in;

import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;

public interface GetSearchUseCase {
    TagHistoryResponseDto searchTags(Long userId, String query);
}
