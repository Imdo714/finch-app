package com.joojoo.api.search.presentation.dto.response.ticker;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class TickerHistoryResponseDto {
    private List<TickerElement> history;

    public static TickerHistoryResponseDto of(List<TickerElement> history) {
        return new TickerHistoryResponseDto(history);
    }

    public static TickerHistoryResponseDto from(Set<String> rawStrings) {
        if (rawStrings == null || rawStrings.isEmpty()) {
            return TickerHistoryResponseDto.of(Collections.emptyList());
        }

        List<TickerElement> elements = rawStrings.stream()
                .map(TickerElement::fromRawString)
                .filter(Objects::nonNull)
                .collect(
                        Collectors.toMap(
                                TickerElement::getTickerId,
                                element -> element,
                                (firstFound, nextFound) -> firstFound,
                                LinkedHashMap::new
                        )
                )
                .values()
                .stream()
                .toList();

        return TickerHistoryResponseDto.of(elements);
    }

    @Getter
    @AllArgsConstructor
    public static class TickerElement {
        private Long tickerId;
        private String name;
        private String symbol;

        public static TickerElement fromRawString(String raw) {
            try {
                // {userId}:{jaso}*{name}*{symbol}*{tickerId}
                // ":"로 분리하여 데이터 파트 추출
                String[] parts = raw.split(":");
                if (parts.length < 2) return null;

                // "*"로 분리 (상세 데이터 추출)
                String[] details = parts[1].split("\\*");

                return new TickerElement(
                        Long.parseLong(details[3]),
                        details[1],
                        details[2]
                );
            } catch (Exception e) {
                return null;
            }
        }
    }
}
