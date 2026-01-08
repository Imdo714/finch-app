package com.joojoo.api.search.infrastructure.rdbms;

import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.infrastructure.queryDsl.SearchQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchRepository {

    private final SearchJpaRepository searchJpaRepository;
    private final SearchQueryDslRepository searchQueryDslRepository;

    @Override
    public void save(SearchHistory history) {
        searchJpaRepository.save(history);
    }

    @Override
    public List<SearchHistory> findRecentByTargetType(Long userId, SearchTarget type, int limitSize) {
        return searchQueryDslRepository.findRecentByTargetType(userId, type, limitSize);
    }

    @Override
    public void deleteDuplicateHistory(SearchHistory newHistory) {
        searchQueryDslRepository.deleteDuplicateHistory(newHistory);
    }
}
