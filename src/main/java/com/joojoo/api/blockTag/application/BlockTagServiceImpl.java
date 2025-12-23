package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.RecentTagsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockTagServiceImpl implements BlockTagService {

    private final BlockTagRepository blockTagRepository;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }

}
