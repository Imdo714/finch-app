package com.joojoo.api.ticker.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum MarketType {

    KOSPI("코스피"),
    KOSDAQ("코스닥"),
    KOSDAQ_GLOBAL("코스닥 글로벌"),
    KONEX("코넥스"),
    NASDAQ("나스닥"),
    AMEX("아멕스"),
    NYSE("뉴욕증권"),
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

    /** 국내 시장 리스트 반환 */
    public static List<MarketType> koreaMarkets() {
        return List.of(KOSPI, KOSDAQ, KONEX, KOSDAQ_GLOBAL);
    }
}
