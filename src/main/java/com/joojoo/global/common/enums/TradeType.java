package com.joojoo.global.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeType {
    BUY("매수"),
    SELL("매도"),
    ;

    private final String text;
}
