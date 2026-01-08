package com.joojoo.api.common.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeType {
    BUY("매수"),
    SELL("매도"),
    ;

    private final String text;

    @JsonCreator
    public static TradeType from(String value) {
        for (TradeType type : TradeType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
