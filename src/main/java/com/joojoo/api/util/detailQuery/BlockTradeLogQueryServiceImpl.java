package com.joojoo.api.util.detailQuery;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.presentation.dto.response.all.BlockDetailMode;
import com.joojoo.api.blockTag.presentation.dto.response.all.DailyGroupResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.all.TradeLogResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.util.detailQuery.dto.BlockRelatedDataBundle;
import com.joojoo.api.util.detailQuery.service.BlockDataFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockTradeLogQueryServiceImpl implements BlockTradeLogQueryService {

    private final BlockDataFetcher blockDataFetcher;

    @Override
    public BlockTagsResponse assembleBlockTagsResponse(List<LocalDate> targetDates, List<Block> allBlocks, List<TradeLog> allTradeLogs) {
        /** ID 추출 */
        List<Long> blockIds = allBlocks.stream().map(Block::getId).toList();
        List<Long> tradeLogIds = allTradeLogs.stream().map(TradeLog::getId).toList();

        /** 연관 데이터 일괄 조회 */
        BlockRelatedDataBundle bundle = blockDataFetcher.fetchRelatedData(blockIds, tradeLogIds);

        /** 날짜별 그룹화 DTO만들기 */
        Map<LocalDate, List<BlockDetailMode>> groupedBlocks = groupBlocksByDate(allBlocks, bundle);
        Map<LocalDate, List<TradeLogResponseDto>> groupedTradeLogs = groupTradeLogsByDate(allTradeLogs, bundle);

        /** DailyGroup 날짜, block, TradeLog DTO 변환 */
        List<DailyGroupResponseDto> dailyGroups = targetDates.stream()
                .limit(2)
                .map(date -> DailyGroupResponseDto.of(date, groupedBlocks, groupedTradeLogs))
                .toList();

        return buildBlockTagsResponse(targetDates, dailyGroups);
    }

    private Map<LocalDate, List<BlockDetailMode>> groupBlocksByDate(List<Block> blocks, BlockRelatedDataBundle bundle) {
        return blocks.stream()
                .map(b -> BlockDetailMode.from(b,
                        bundle.getTagsByBlockId().getOrDefault(b.getId(), Collections.emptyList()),
                        bundle.getTickersByBlockId().getOrDefault(b.getId(), Collections.emptyList()),
                        bundle.getChildCounts().getOrDefault(b.getId(), 0L)))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));
    }

    private Map<LocalDate, List<TradeLogResponseDto>> groupTradeLogsByDate(List<TradeLog> logs, BlockRelatedDataBundle bundle) {
        return logs.stream()
                .map(log -> TradeLogResponseDto.from(log,
                        bundle.getTagsByTradeLogId().getOrDefault(log.getId(), Collections.emptyList()),
                        bundle.getTickerByTradeLogId().getOrDefault(log.getId(), Collections.emptyList())
                ))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));
    }

    private BlockTagsResponse buildBlockTagsResponse(List<LocalDate> targetDates, List<DailyGroupResponseDto> dailyGroups) {
        boolean hasNext = targetDates.size() > 2;
        LocalDate nextDate = hasNext ? targetDates.get(2) : null;
        return BlockTagsResponse.of(dailyGroups, hasNext, nextDate);
    }
}
