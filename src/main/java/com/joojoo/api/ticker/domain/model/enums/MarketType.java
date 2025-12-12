package com.joojoo.api.ticker.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketType {
    
    KOSPI("코스피"),
    NASDAQ("나스닥큐"),
    ;

    private final String text;
}
