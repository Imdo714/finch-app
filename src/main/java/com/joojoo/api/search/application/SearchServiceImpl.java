package com.joojoo.api.search.application;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.presentation.dto.request.RecentSearchDto;
import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.common.enums.SearchTarget;
import com.joojoo.global.exception.handleException.tags.TagNotFoundException;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final BlockTagRepository blockTagRepository;
    private final SearchRepository searchRepository;
    private final TickerRepository tickerRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;


    @Override
    public TagHistoryResponseDto searchTags(Long userId, String query) {
        if (!StringUtils.hasText(query)) return TagHistoryResponseDto.of(Collections.emptyList());

        String convertedQuery = HangulUtils.splitToJaso(query); // 자성으로 변환해서 검색
        String prefix = userId + ":" + convertedQuery;

        Set<String> searchTagQuery = blockTagRepository.searchTagQuery(prefix);
        if (searchTagQuery == null || searchTagQuery.isEmpty()) {
            return TagHistoryResponseDto.of(Collections.emptyList());
        }

        return TagHistoryResponseDto.from(searchTagQuery);
    }

    @Override
    @Transactional
    public void recordSearch(Long userId, SearchRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (dto.getTargetType() == SearchTarget.TICKER) {
            tickerRepository.findById(dto.getTargetId())
                    .ifPresentOrElse(
                            ticker -> saveHistory(user, SearchTarget.TICKER, null, ticker),
                            () -> { throw new TickerNotFoundException(); }
                    );
        } else if (dto.getTargetType() == SearchTarget.TAG) {
            tagRepository.findById(dto.getTargetId())
                    .ifPresentOrElse(
                            tag -> saveHistory(user, SearchTarget.TAG, tag, null),
                            () -> { throw new TagNotFoundException(); }
                    );
        }
    }

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

    private void saveHistory(User user, SearchTarget type, Tag tag, Ticker ticker) {
        searchRepository.deleteIfExists(user.getId(), type, tag, ticker);
        searchRepository.save(SearchHistory.of(user, type, tag, ticker));
    }

}
