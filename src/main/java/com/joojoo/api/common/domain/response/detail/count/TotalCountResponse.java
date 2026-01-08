package com.joojoo.api.common.domain.response.detail.count;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TotalCountResponse {
    private String name;
    private Long count;
}
