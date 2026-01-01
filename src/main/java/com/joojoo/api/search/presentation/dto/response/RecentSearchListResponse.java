package com.joojoo.api.search.presentation.dto.response;

import com.joojoo.api.search.presentation.dto.request.RecentSearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class RecentSearchListResponse {
    private List<RecentSearchDto> tickers;
    private List<RecentSearchDto> tags;
}
