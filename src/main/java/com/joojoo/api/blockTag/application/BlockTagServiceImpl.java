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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        List<BlockTag> blockTags = result.getContent();
        List<LocalDate> targetDates = result.getTargetDates();

        if (blockTags.isEmpty()) {
            return BlockTagsResponse.of(Collections.emptyList(), false, null);
        }
        List<LocalDate> displayDates = targetDates.stream().limit(2).toList();

        List<BlockTag> displayBlockTags = blockTags.stream()
                .filter(bt -> {
                    LocalDate date = (bt.getBlock() != null)
                            ? bt.getBlock().getCreatedAt().toLocalDate()
                            : bt.getTradeLog().getCreatedAt().toLocalDate();
                    return displayDates.contains(date);
                })
                .toList();

        // 엔티티 추출 및 ID 리스트 생성
        List<Block> blocks = displayBlockTags.stream()
                .map(BlockTag::getBlock).filter(Objects::nonNull).distinct().toList();
        List<TradeLog> tradeLogs = displayBlockTags.stream()
                .map(BlockTag::getTradeLog).filter(Objects::nonNull).distinct().toList();

        List<Long> blockIds = blocks.stream().map(Block::getId).toList();
        List<Long> tradeLogIds = tradeLogs.stream().map(TradeLog::getId).toList();

        // 연관 데이터 조회
        /** blockId 로 연관퇸 블럭 태그들 조회 */
        Map<Long, List<BlockTag>> tagsByBlockId = blockTagRepository.findAllBlockTags(blockIds).stream()
                .collect(Collectors.groupingBy(bt -> bt.getBlock().getId()));

        /** blockId 로 연관퇸 블럭 티커들 조회 */
        Map<Long, List<BlockTicker>> tickersByBlockId = blockTickerRepository.findAllBlockTickers(blockIds).stream()
                .collect(Collectors.groupingBy(bt -> bt.getBlock().getId()));

        /** 조회한 블럭의 자식 갯수 */
        Map<Long, Long> childCounts = blockRepository.getChildCounts(blockIds);

        /** tradeLogId 로 연관퇸 템플릿 태그들 조회 */
        Map<Long, List<BlockTag>> tagsByTradeLogId = queryDsl.findAllByTradeLogIds(tradeLogIds).stream()
                .collect(Collectors.groupingBy(bt -> bt.getTradeLog().getId()));

        /** tradeLogId 로 연관퇸 템플릿 티커들 조회 */
        Map<Long, List<BlockTicker>> tickerByTradeLogId = queryDsl.findAllTickersByTradeLogIds(tradeLogIds).stream()
                .collect(Collectors.groupingBy(bt -> bt.getTradeLog().getId()));

        // DTO 변환 및 날짜별 그룹화
        Map<LocalDate, List<BlockDetailMode>> groupedBlocks = blocks.stream()
                .map(b -> BlockDetailMode.from(b,
                        tagsByBlockId.getOrDefault(b.getId(), Collections.emptyList()),
                        tickersByBlockId.getOrDefault(b.getId(), Collections.emptyList()),
                        childCounts.getOrDefault(b.getId(), 0L)))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));

        Map<LocalDate, List<TradeLogResponseDto>> groupedTradeLogs = tradeLogs.stream()
                .map(log -> TradeLogResponseDto.from(log,
                        tagsByTradeLogId.getOrDefault(log.getId(), Collections.emptyList()),
                        tickerByTradeLogId.getOrDefault(log.getId(), Collections.emptyList())
                ))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));

        List<DailyGroupResponseDto> dailyGroups = targetDates.stream()
                .limit(2)
                .map(date -> DailyGroupResponseDto.of(date, groupedBlocks, groupedTradeLogs))
                .toList();

        boolean hasNext = targetDates.size() > 2;
        LocalDate nextDate = hasNext ? targetDates.get(2) : null;

        return BlockTagsResponse.of(dailyGroups, hasNext, nextDate);
    }


}
