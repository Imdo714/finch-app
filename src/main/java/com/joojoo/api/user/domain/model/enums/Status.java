package com.joojoo.api.user.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {

    ACTIVE("활동"),
    DELETED("탈퇴"),
    ;

    private final String text;
}
