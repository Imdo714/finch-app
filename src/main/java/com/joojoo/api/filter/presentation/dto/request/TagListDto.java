package com.joojoo.api.filter.presentation.dto.request;

import com.joojoo.api.common.domain.enums.FilterCategory;
import com.joojoo.global.exception.handleException.filter.FilterInputInvalidException;
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

    public void validateHasKeywords() {
        if (isTagEmpty() && isTickerEmpty()) {
            throw new FilterInputInvalidException();
        }
    }

    private boolean isTagEmpty() {
        return tagIds == null || tagIds.isEmpty();
    }

    private boolean isTickerEmpty() {
        return tickerIds == null || tickerIds.isEmpty();
    }

}
