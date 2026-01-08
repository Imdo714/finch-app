package com.joojoo.api.search.application.service.command;

import com.joojoo.api.search.application.port.in.CreateSearchUseCase;
import com.joojoo.api.search.domain.entity.SearchHistory;
import com.joojoo.api.search.domain.service.SearchRecordService;
import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateSearchService implements CreateSearchUseCase {

    private final TickerRepository tickerRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final SearchRecordService searchRecordService;

    @Override
    @Transactional
    public void recordSearch(Long userId, SearchRequestDto searchRequestDto) {
        User user = userRepository.getUserById(userId);

        SearchHistory history = getSearchHistory(searchRequestDto, user);
        searchRecordService.record(history);
    }

    private SearchHistory getSearchHistory(SearchRequestDto dto, User user) {
        return switch (dto.getTargetType()) {
            case TICKER -> {
                Ticker ticker = tickerRepository.findById(dto.getTargetId())
                        .orElseThrow(TickerNotFoundException::new);
                yield SearchHistory.createTickerHistory(user, ticker);
            }
            case TAG -> {
                Tag tag = tagRepository.findById(dto.getTargetId())
                        .orElseThrow(TagNotFoundException::new);
                yield SearchHistory.createTagHistory(user, tag);
            }
        };
    }

}
