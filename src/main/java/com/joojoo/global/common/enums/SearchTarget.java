package com.joojoo.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchTarget {
    TAG("태그"),
    TICKER("티커"),
    ;

    private final String text;

    @JsonCreator
    public static SearchTarget from(String value) {
        for (SearchTarget type : SearchTarget.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
