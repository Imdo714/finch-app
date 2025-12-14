package com.joojoo.api.ticker.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketType {

    KOSPI("코스피"),
    KOSDAQ("코스닥"),
    KOSDAQ_GLOBAL("코스닥 글로벌"),
    KONEX("코넥스"),
    ETC("기타"),
    ;

    private final String text;

    public static MarketType fromString(String market) {
        if (market == null || market.isBlank()) {
            return ETC;
        }

        String normalizedMarket = market.toUpperCase().replace(" ", "_");
        try {
            return MarketType.valueOf(normalizedMarket);
        } catch (IllegalArgumentException e) {
            return ETC;
        }
    }
}
