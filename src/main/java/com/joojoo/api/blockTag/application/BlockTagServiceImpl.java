package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import com.joojoo.global.exception.handleException.tags.TagNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockTagServiceImpl implements BlockTagService {

    private final BlockTagRepository blockTagRepository;
    private final TagRepository tagRepository;

    @Override
    public TotalCountResponse getBlockCount(Long userId, Long tagId) {
        Tag tag = tagRepository.findById(userId)
                .orElseThrow(TagNotFoundException::new);

        BlockTagCountResponse blockCount = blockTagRepository.getBlockCount(userId, tagId);
        return new TotalCountResponse(blockCount.getName(), blockCount.getTotalCount());
    }

}
