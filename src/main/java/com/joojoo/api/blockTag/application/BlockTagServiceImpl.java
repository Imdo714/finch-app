package com.joojoo.api.blockTag.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.util.detailQuery.BlockTradeLogQueryService;
import com.joojoo.global.exception.handleException.tags.TagNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockTagServiceImpl implements BlockTagService {

    private final BlockTagRepository blockTagRepository;
    private final BlockTreeValidator blockTreeValidator;
    private final BlockTradeLogQueryService blockTradeLogQueryService;
    private final TagRepository tagRepository;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }

    @Override
    public TotalCountResponse getBlockCount(Long userId, Long tagId) {
        Tag tag = tagRepository.findById(userId)
                .orElseThrow(TagNotFoundException::new);

        BlockTagCountResponse blockCount = blockTagRepository.getBlockCount(userId, tagId);
        return new TotalCountResponse(blockCount.getName(), blockCount.getTotalCount());
    }

    @Override
    public BlockTagsResponse getUserTagIdsByTagId(Long userId, Long tagId, LocalDate lastDate) {
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        TagDateResult result = blockTagRepository.findAllByTagAndDate(userId, tagId, targetDate);

        if (result.getContent().isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
        }

        List<LocalDate> displayDates = result.getTargetDates().stream().limit(2).toList();

        List<Block> blocks = result.getContent().stream()
                .filter(bt -> displayDates.contains(getCreatedAt(bt).toLocalDate()))
                .map(BlockTag::getBlock)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<TradeLog> tradeLogs = result.getContent().stream()
                .filter(bt -> displayDates.contains(getCreatedAt(bt).toLocalDate()))
                .map(BlockTag::getTradeLog)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return blockTradeLogQueryService.assembleBlockTagsResponse(result.getTargetDates(), blocks, tradeLogs);
    }

    /** 엔티티로부터 생성일자를 추출하는 헬퍼 메서드 */
    private LocalDateTime getCreatedAt(BlockTag bt) {
        return (bt.getBlock() != null) ? bt.getBlock().getCreatedAt() : bt.getTradeLog().getCreatedAt();
    }

}
