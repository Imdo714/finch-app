package com.joojoo.api.blockTag.application.service.query;

import com.joojoo.api.blockTag.application.port.in.GetBlockTagUseCase;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBlockTagService implements GetBlockTagUseCase {

    private final BlockTagRepository blockTagRepository;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }
}
