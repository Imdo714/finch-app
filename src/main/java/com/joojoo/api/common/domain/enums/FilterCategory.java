package com.joojoo.api.common.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilterCategory {
    ALL("전체"),
    BLOCK("블럭"),
    BUY("매수"),
    SELL("매도"),
    ;

    private final String text;

    @JsonCreator
    public static FilterCategory from(String value) {
        for (FilterCategory type : FilterCategory.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    public boolean isBlockApplicable() {
        return this == ALL || this == BLOCK;
    }

    public boolean isTradeLogApplicable() {
        return this == ALL || this == BUY || this == SELL;
    }
}
