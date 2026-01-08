package com.joojoo.api.search.domain.repository;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.common.domain.enums.SearchTarget;

import java.util.List;

public interface SearchRepository {
    void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker);

    void save(SearchHistory history);
    
    /** 최근 검색 리스트 조회 */
    List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize);
}
