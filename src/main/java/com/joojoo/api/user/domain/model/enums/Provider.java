package com.joojoo.api.user.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {

    KAKAO("Kakao"),
    APPLE("apple"),
    ;

    private final String text;
}
