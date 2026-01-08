package com.joojoo.api.common.domain.response.detail.count;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RelatedBlockDetailCountResponse {
    private String name;
    private Long blockCount;
    private Long tradeLogCount;

    public Long getTotalCount() {
        return (blockCount != null ? blockCount : 0L) + (tradeLogCount != null ? tradeLogCount : 0L);
    }
}
