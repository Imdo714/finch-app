package com.joojoo.api.ticker.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
public class TickerSearchResponse {
    private List<TickerSearchList> tickers;

    @Getter
    @AllArgsConstructor
    public static class TickerSearchList {
        private String name;
        private String ticker;
    }

    public static TickerSearchResponse of(Set<String> redisResults) {
        List<TickerSearchList> searchList = new ArrayList<>(redisResults.stream()
                .map(TickerSearchResponse::parseRedisData)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        TickerSearchList::getTicker,
                        dto -> dto,
                        (oldValue, newValue) -> oldValue
                ))
                .values()
        );

        return TickerSearchResponse.builder()
                .tickers(searchList)
                .build();
    }

    private static TickerSearchList parseRedisData(String value) {
        String[] parts = value.split("\\*");
        if (parts.length < 3) return null; // 잘못 된 형식은 패스
        return new TickerSearchList(parts[1], parts[2]);
    }

}
