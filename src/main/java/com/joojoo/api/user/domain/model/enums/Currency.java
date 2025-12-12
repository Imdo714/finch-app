package com.joojoo.api.user.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {

    KRW("KRW"),
    USD("USD"),
    ;

    private final String text;
}
