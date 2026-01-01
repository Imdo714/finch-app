package com.joojoo.api.search.domain.repository;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.global.common.enums.SearchTarget;

public interface SearchRepository {
    void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker);

    void save(SearchHistory history);
}
