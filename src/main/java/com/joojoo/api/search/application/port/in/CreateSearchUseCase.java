package com.joojoo.api.search.application.port.in;

import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;

public interface CreateSearchUseCase {
    void recordSearch(Long userId, SearchRequestDto requestDto);
}
