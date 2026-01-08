package com.joojoo.api.search.infrastructure.queryDsl;

import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.search.domain.entity.SearchHistory;

import java.util.List;

public interface SearchQueryDslRepository {
    List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize);

    void deleteDuplicateHistory(SearchHistory newHistory);
}
