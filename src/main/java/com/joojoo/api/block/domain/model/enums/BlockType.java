package com.joojoo.api.block.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockType {
    TEXT("글"),
    IMAGE("이미지"),
    TRADE("거래 정보"),
    ;

    private final String text;
}
