package com.joojoo.api.search.application;

import com.joojoo.api.common.domain.enums.SearchTarget;
import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.repository.SearchRepository;
import com.joojoo.api.search.presentation.dto.request.RecentSearchDto;
import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import com.joojoo.api.ticker.domain.repository.TickerRepository;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.tags.TagNotFoundException;
import com.joojoo.global.exception.handleException.tickers.TickerNotFoundException;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
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
    private final TickerRepository tickerRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

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
