package com.joojoo.api.search.infrastructure.queryDsl;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.global.common.enums.SearchTarget;

import java.util.List;

public interface SearchQueryDslRepository {
    void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker);

    List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize);
}
