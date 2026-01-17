package com.joojoo.api.common.search;

import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.api.common.hangul.HangulConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchRangeFactory {

    private final HangulConverter hangulConverter;
    private static final String END_UNICODE = "\uffff";

    public SearchRange createSearchRange(String query) {
        String converted = hangulConverter.jasoConvert(query).toUpperCase();
        return buildRange(converted);
    }

    public SearchRange createSearchTagKeyRange(String query, Long userId) {
        String converted = hangulConverter.jasoConvert(query);
        String convertedQuery = userId + ":" + converted;
        return buildRange(convertedQuery);
    }

    private SearchRange buildRange(String start) {
        return new SearchRange(start, start + END_UNICODE);
    }

}
