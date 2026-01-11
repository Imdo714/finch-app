package com.joojoo.api.blockTicker.presentation.dto.response.recent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecentTickersResponse {
    private List<RecentTickersDto> tags;

    public static RecentTickersResponse of(List<RecentTickersDto> tickers) {
        return new RecentTickersResponse(tickers);
    }

    @Getter
    @AllArgsConstructor
    public static class RecentTickersDto {
        private Long tickerId;
        private String tickerName;
        private String symbol;
    }

}
