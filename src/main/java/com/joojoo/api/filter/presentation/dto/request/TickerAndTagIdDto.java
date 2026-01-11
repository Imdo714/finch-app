package com.joojoo.api.filter.presentation.dto.request;

import com.joojoo.global.exception.handleException.filter.FilterInputDuplicateException;
import com.joojoo.global.exception.handleException.filter.FilterInputInvalidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TickerAndTagIdDto {
    private Long tagId;
    private Long tickerId;

    public void validate() {
        if (tagId == null && tickerId == null) {
            throw new FilterInputInvalidException();
        }
        if (tagId != null && tickerId != null) {
            throw new FilterInputDuplicateException();
        }
    }

    public boolean isTagSearch() {
        return tagId != null;
    }
}
