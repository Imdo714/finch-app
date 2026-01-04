package com.joojoo.api.blockTag.application.service.query;

import com.joojoo.api.blockTag.application.port.in.GetBlockTagUseCase;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.domain.service.assembler.BlockTagAssembler;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.util.date.DateUtils;
import com.joojoo.global.exception.handleException.tags.TagNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockTagQueryService implements GetBlockTagUseCase {

    private final BlockTagRepository blockTagRepository;
    private final DateUtils dateUtils;
    private final BlockTagAssembler blockTagAssembler;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }

    @Override
    public BlockTagsResponse getBlockTagDetail(Long userId, Long tagId, LocalDate lastDate) {
        LocalDate targetDate = dateUtils.validateAndGetTargetDate(lastDate);
        TagDateResult result = blockTagRepository.findAllByTagAndDate(userId, tagId, targetDate);

        if (result.getContent().isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
        }

        List<LocalDate> displayDates = result.getTargetDates().stream().limit(2).toList();
        return blockTagAssembler.assembleBlockTagsResponse(result, displayDates);
    }

    @Override
    public TotalCountResponse getBlockCount(Long userId, Long tagId) {
        BlockTagCountResponse blockCount = blockTagRepository.getBlockCount(userId, tagId);

        if (blockCount == null) {
            throw new TagNotFoundException();
        }

        return new TotalCountResponse(blockCount.getName(), blockCount.getTotalCount());
    }

}
