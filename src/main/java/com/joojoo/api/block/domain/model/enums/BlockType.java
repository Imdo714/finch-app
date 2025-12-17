package com.joojoo.api.block.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BlockType {
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    ;

    private final String text;
}
