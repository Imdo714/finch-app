package com.joojoo.api.blockTag.application;

import com.joojoo.api.block.application.detail.BlockDtoAssembler;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockTagServiceImpl implements BlockTagService {

    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final BlockRepository blockRepository;

    private final BlockDtoAssembler blockDtoAssembler;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }

    @Override
    public BlockTagsResponse getUserTagIdsByTagId(Long userId, Long tagId, Long lastBlockId, int pageSize) {
        List<Block> blocksByTagIds = blockRepository.getBlocksByTagId(userId, tagId, lastBlockId, pageSize);
        if (blocksByTagIds.isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
        }

        boolean hasNext = isHasNext(blocksByTagIds, pageSize);

        List<Long> blockIds = blockDtoAssembler.toIds(blocksByTagIds);
        List<BlockTag> tags = blockTagRepository.findAllBlockTags(blockIds);
        List<BlockTicker> tickers = blockTickerRepository.findAllBlockTickers(blockIds);
        Map<Long, Long> childCounts = blockRepository.getChildCounts(blockIds);

        List<BlockDetailResponseDto> allDtos = blockDtoAssembler.assembleMainList(blocksByTagIds, tags, tickers, childCounts);
        Long nextLastBlockId = hasNext ? blocksByTagIds.get(blocksByTagIds.size() - 1).getId() : null;

        return BlockTagsResponse.of(allDtos, hasNext, nextLastBlockId);
    }

    @Override
    public BlockTagCountResponse getBlockCount(Long userId, Long tagId) {
         return blockTagRepository.getBlockCount(userId, tagId);
    }

    private boolean isHasNext(List<Block> blocksByTagIds, int pageSize) {
        boolean hasNext = blocksByTagIds.size() > pageSize;

        if (hasNext) {
            blocksByTagIds.remove(pageSize);
        }
        return hasNext;
    }

}
