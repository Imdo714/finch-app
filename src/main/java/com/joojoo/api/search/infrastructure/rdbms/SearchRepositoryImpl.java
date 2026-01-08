package com.joojoo.api.search.infrastructure.rdbms;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.infrastructure.queryDsl.SearchQueryDslRepository;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.common.domain.enums.SearchTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    private final SearchJpaRepository searchJpaRepository;
    private final SearchQueryDslRepository searchQueryDslRepository;

    @Override
    public void deleteIfExists(Long userId, SearchTarget type, Tag tag, Ticker ticker) {
        searchQueryDslRepository.deleteIfExists(userId, type, tag, ticker);
    }

    @Override
    public void save(SearchHistory history) {
        searchJpaRepository.save(history);
    }

    @Override
    public List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize) {
        return searchQueryDslRepository.findRecentByTargetType(userId, type, limitSize);
    }
}
