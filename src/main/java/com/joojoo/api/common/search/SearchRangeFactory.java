package com.joojoo.api.common.search;

import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.api.util.port.in.JasoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchRangeFactory {

    private final JasoConverter jasoConverter;
    private static final String END_UNICODE = "\uffff";

    public SearchRange createSearchRange(String query) {
        String converted = jasoConverter.convert(query);
        return buildRange(converted);
    }

    public SearchRange createSearchTagKeyRange(String query, Long userId) {
        String converted = jasoConverter.convert(query);
        String convertedQuery = userId + ":" + converted;
        return buildRange(convertedQuery);
    }

    private SearchRange buildRange(String start) {
        return new SearchRange(start, start + END_UNICODE);
    }

}
