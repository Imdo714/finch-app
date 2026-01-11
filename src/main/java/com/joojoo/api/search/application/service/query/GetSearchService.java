package com.joojoo.api.search.application.service.query;

import com.joojoo.api.blockTag.infrastructure.redis.BlockTagRedisRepository;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.infrastructure.redis.BlockTickerRedisRepository;
import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.common.search.SearchRangeFactory;
import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.api.search.application.port.in.GetSearchUseCase;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.presentation.dto.request.RecentSearchDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import com.joojoo.api.search.presentation.dto.response.ticker.TickerHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSearchService implements GetSearchUseCase {

    private final SearchRangeFactory searchRangeFactory;
    private final SearchRepository searchRepository;
    private final BlockTagRedisRepository blockTagRedisRepository;
    private final BlockTickerRedisRepository blockTickerRedisRepository;

    @Override
    public TagHistoryResponseDto searchTags(Long userId, String query) {
        if (!StringUtils.hasText(query)) return TagHistoryResponseDto.of(Collections.emptyList());

        SearchRange range = searchRangeFactory.createSearchTagKeyRange(query, userId);
        return TagHistoryResponseDto.from(blockTagRedisRepository.searchTagQuery(range));
    }

    @Override /** 사용자가 사용한 티커 조회 고도화 때 사용할 예정 */
    public TickerHistoryResponseDto searchTickers(Long userId, String query) {
        if (!StringUtils.hasText(query)) return TickerHistoryResponseDto.of(Collections.emptyList());
        SearchRange range = searchRangeFactory.createSearchTagKeyRange(query, userId);
        return TickerHistoryResponseDto.from(blockTickerRedisRepository.searchTickersQuery(range));
    }

    @Override
    public RecentSearchListResponse recordSearchList(Long userId) {
        return RecentSearchListResponse.builder()
                .tickers(fetchRecent(userId, SearchTarget.TICKER))
                .tags(fetchRecent(userId, SearchTarget.TAG))
                .build();
    }

    private List<RecentSearchDto> fetchRecent(Long userId, SearchTarget type) {
        return searchRepository.findRecentByTargetType(userId, type, 10)
                .stream()
                .map(RecentSearchDto::of)
                .toList();
    }

}
