package com.joojoo.api.common.domain.enums;

import lombok.Getter;

@Getter
public enum TagSourceType {
    BLOCK_CONTENT("블록 내용"),
    TRADE_HEADER("거래내역 용"),
    TRADE_MEMO("매매 근거"),
    TRADE_RISK("리스크 요소"),
    TRADE_PLAN("매매 계획");

    private final String description;

    TagSourceType(String description) {
        this.description = description;
    }
}
