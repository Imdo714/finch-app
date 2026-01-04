package com.joojoo.api.blockTag.domain.service.assembler;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.application.port.out.BlockDataFetcher;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.all.BlockDetailMode;
import com.joojoo.api.blockTag.presentation.dto.response.all.DailyGroupResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.all.TradeLogResponseDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.api.blockTag.presentation.dto.request.BlockRelatedDataBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BlockTagAssembler {

    private final BlockDataFetcher blockDataFetcher;

    /** TagDateResult를 바탕으로 Response를 필터링하고 조립 */
    public BlockTagsResponse assembleBlockTagsResponse(TagDateResult result, List<LocalDate> displayDates) {
        /** 블록 추출 및 필터링 */
        List<Block> blocks = result.getContent().stream()
                .filter(bt -> displayDates.contains(bt.getRelevantCreatedDate()))
                .map(BlockTag::getBlock)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        /** 트레이드로그 추출 및 필터링 */
        List<TradeLog> tradeLogs = result.getContent().stream()
                .filter(bt -> displayDates.contains(bt.getRelevantCreatedDate()))
                .map(BlockTag::getTradeLog)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return assembleBlockTagsResponse(result.getTargetDates(), blocks, tradeLogs);
    }

    /** 주어진 날짜 리스트와 블록/트레이드로그 목록을 응답용 DTO를 조립합니다. */
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

    /** 블록 엔티티들을 번들 데이터와 매핑하여 DTO로 변환하고, 생성일자(LocalDate) 기준으로 그룹화합니다. */
    private Map<LocalDate, List<BlockDetailMode>> groupBlocksByDate(List<Block> blocks, BlockRelatedDataBundle bundle) {
        return blocks.stream()
                .map(b -> BlockDetailMode.from(b,
                        bundle.getTagsByBlockId().getOrDefault(b.getId(), Collections.emptyList()),
                        bundle.getTickersByBlockId().getOrDefault(b.getId(), Collections.emptyList()),
                        bundle.getChildCounts().getOrDefault(b.getId(), 0L)))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));
    }

    /** 트레이드로그 엔티티들을 번들 데이터와 매핑하여 DTO로 변환하고, 생성일자(LocalDate) 기준으로 그룹화합니다. */
    private Map<LocalDate, List<TradeLogResponseDto>> groupTradeLogsByDate(List<TradeLog> logs, BlockRelatedDataBundle bundle) {
        return logs.stream()
                .map(log -> TradeLogResponseDto.from(log,
                        bundle.getTagsByTradeLogId().getOrDefault(log.getId(), Collections.emptyList()),
                        bundle.getTickerByTradeLogId().getOrDefault(log.getId(), Collections.emptyList())
                ))
                .collect(Collectors.groupingBy(dto -> dto.getCreatedAt().toLocalDate()));
    }

    /** 조회된 전체 날짜 목록을 확인하여 다음 페이지 존재 여부와 다음 조회 시작 날짜를 계산합니다. */
    private BlockTagsResponse buildBlockTagsResponse(List<LocalDate> targetDates, List<DailyGroupResponseDto> dailyGroups) {
        boolean hasNext = targetDates.size() > 2;
        LocalDate nextDate = hasNext ? targetDates.get(2) : null;
        return BlockTagsResponse.of(dailyGroups, hasNext, nextDate);
    }

}
