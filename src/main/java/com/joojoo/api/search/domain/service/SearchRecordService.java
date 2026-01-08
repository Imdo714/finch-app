package com.joojoo.api.search.domain.service;

import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchRecordService {

    private final SearchRepository searchRepository;

    public void record(SearchHistory newHistory) {
        searchRepository.deleteDuplicateHistory(newHistory);
        searchRepository.save(newHistory);
    }

}
