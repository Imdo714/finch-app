package com.joojoo.api.block.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeleteMode {
    SINGLE("자신만 삭제"),
    ALL("전체 삭제")
    ;

    private final String text;

    public boolean isAll(){
        return this == ALL;
    }
}
