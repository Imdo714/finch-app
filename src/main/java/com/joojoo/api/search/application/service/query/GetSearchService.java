package com.joojoo.api.search.application.service.query;

import com.joojoo.api.blockTag.infrastructure.redis.BlockTagRedisRepository;
import com.joojoo.api.common.search.SearchRangeFactory;
import com.joojoo.api.common.search.range.SearchRange;
import com.joojoo.api.search.application.port.in.GetSearchUseCase;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSearchService implements GetSearchUseCase {

    private final SearchRangeFactory searchRangeFactory;

    private final BlockTagRedisRepository blockTagRedisRepository;

    @Override
    public TagHistoryResponseDto searchTags(Long userId, String query) {
        if (!StringUtils.hasText(query)) return TagHistoryResponseDto.of(Collections.emptyList());

        SearchRange range = searchRangeFactory.createSearchTagKeyRange(query, userId);
        return TagHistoryResponseDto.from(blockTagRedisRepository.searchTagQuery(range));
    }

}
