package com.joojoo.api.blockTag.presentation.dto.request;

import com.joojoo.global.common.enums.FilterCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagListDto {

    @NotNull(message = "category 타입(ALL/BLOCK/BUY/SELL)은 입니다.")
    private FilterCategory category;
    private List<Long> tagIds;
    private List<Long> tickerIds;
}
