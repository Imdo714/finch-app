package com.joojoo.api.search.infrastructure.queryDsl;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.global.common.enums.SearchTarget;

public interface SearchQueryDslRepository {
    void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker);
}
