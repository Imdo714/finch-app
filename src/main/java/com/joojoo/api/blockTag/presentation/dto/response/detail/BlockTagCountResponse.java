package com.joojoo.api.blockTag.presentation.dto.response.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BlockTagCountResponse {
    private String name;
    private Long blockCount;
    private Long tradeLogCount;

    public Long getTotalCount() {
        return (blockCount != null ? blockCount : 0L) + (tradeLogCount != null ? tradeLogCount : 0L);
    }
}
