package com.joojoo.api.util.detailQuery;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.blockTag.application.test.QueryDsl;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.blockTag.presentation.dto.response.all.BlockDetailMode;
import com.joojoo.api.blockTag.presentation.dto.response.all.DailyGroupResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.all.TradeLogResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
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

    private final BlockTagRepository blockTagRepository;
    private final BlockTickerRepository blockTickerRepository;
    private final BlockRepository blockRepository;
    private final QueryDsl queryDsl;

    @Override
    public BlockTagsResponse assembleBlockTagsResponse(List<LocalDate> targetDates, List<Block> allBlocks, List<TradeLog> allTradeLogs) {
        /** 조회할 Block, TradeLog ID들 추출 */
        List<Long> blockIds = allBlocks.stream().map(Block::getId).toList();
        List<Long> tradeLogIds = allTradeLogs.stream().map(TradeLog::getId).toList();

        // 연관 데이터 일괄 조회
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

        /** 날짜별 그룹화하여 블럭 모음 */
        Map<LocalDate, List<BlockDetailMode>> groupedBlocks = allBlocks.stream()
                .map(b -> BlockDetailMode.from(b,
                        tagsByBlockId.getOrDefault(b.getId(), Collections.emptyList()),
                        tickersByBlockId.getOrDefault(b.getId(), Collections.emptyList()),
                        childCounts.getOrDefault(b.getId(), 0L)))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));

        /** 날짜별 그룹화하여 TradeLog 모음 */
        Map<LocalDate, List<TradeLogResponseDto>> groupedTradeLogs = allTradeLogs.stream()
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
