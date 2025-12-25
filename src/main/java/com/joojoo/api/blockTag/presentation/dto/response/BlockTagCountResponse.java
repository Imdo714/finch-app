package com.joojoo.api.blockTag.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockTagCountResponse {
    private String tagName;
    private Long count;
}
