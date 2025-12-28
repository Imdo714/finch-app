package com.joojoo.api.blockTag.application;

import com.joojoo.api.block.application.detail.BlockDtoAssembler;
import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.application.test.QueryDsl;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.all.BlockDetailMode;
import com.joojoo.api.blockTag.presentation.dto.response.all.DailyGroupResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.all.TradeLogResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.tradeLog.domain.repository.TradeLogRepository;
import com.joojoo.api.util.detailQuery.BlockTradeLogQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockTagServiceImpl implements BlockTagService {

    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final BlockRepository blockRepository;
    private final BlockTreeValidator blockTreeValidator;
    private final BlockTradeLogQueryService blockTradeLogQueryService;

    @Override
    public RecentTagsResponse getRecentTags(Long userId) {
        return RecentTagsResponse.of(blockTagRepository.findRecentTags(userId));
    }

    @Override
    public TotalCountResponse getBlockCount(Long userId, Long tagId) {
        BlockTagCountResponse blockCount = blockTagRepository.getBlockCount(userId, tagId);
        return new TotalCountResponse(blockCount.getName(), blockCount.getTotalCount());
    }

    private boolean isHasNext(List<Block> blocksByTagIds, int pageSize) {
        boolean hasNext = blocksByTagIds.size() > pageSize;

        if (hasNext) {
            blocksByTagIds.remove(pageSize);
        }
        return hasNext;
    }


    private final QueryDsl queryDsl;

    @Override
    public BlockTagsResponse getUserTagIdsByTagId(Long userId, Long tagId, LocalDate lastDate) {
        LocalDate targetDate = blockTreeValidator.validateAndGetTargetDate(lastDate);
        TagDateResult result = queryDsl.findAllByTagAndDate(userId, tagId, targetDate);

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
