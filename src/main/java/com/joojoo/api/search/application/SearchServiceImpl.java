package com.joojoo.api.search.application;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import com.joojoo.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final BlockTagRepository blockTagRepository;

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

}
