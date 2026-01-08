package com.joojoo.api.search.application;

import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.presentation.dto.request.RecentSearchDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    @Transactional(readOnly = true)
    public RecentSearchListResponse recordSearchList(Long userId) {
        List<RecentSearchDto> tickerList = searchRepository
                .findRecentByTargetType(userId, SearchTarget.TICKER, 10)
                .stream()
                .map(RecentSearchDto::of)
                .collect(Collectors.toList());

        List<RecentSearchDto> tagList = searchRepository
                .findRecentByTargetType(userId, SearchTarget.TAG, 10)
                .stream()
                .map(RecentSearchDto::of)
                .collect(Collectors.toList());

        return RecentSearchListResponse.builder()
                .tickers(tickerList)
                .tags(tagList)
                .build();
    }

}
