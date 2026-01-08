package com.joojoo.api.search.presentation.dto.request;

import com.joojoo.api.common.domain.enums.SearchTarget;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchRequestDto {
    @NotNull(message = "category 타입(TAG/TICKER)은 입니다.")
    private SearchTarget targetType;
     private Long targetId;
}
