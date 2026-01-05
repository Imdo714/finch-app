package com.joojoo.api.ticker.domain.service.assembler;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.global.common.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.assembler.detailApiAssembler.DetailResponseAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BlockTickerAssembler {

    private final DetailResponseAssembler detailResponseAssembler;

    public DailyBlockDetailsResponse assembleBlockTickersResponse(TickerDateResult result, List<LocalDate> displayDates) {
        /** 블록 추출 및 필터링 */
        List<Block> blocks = result.getContent().stream()
                .filter(bt -> displayDates.contains(bt.getRelevantCreatedDate()))
                .map(BlockTicker::getBlock)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        /** 트레이드로그 추출 및 필터링 */
        List<TradeLog> tradeLogs = result.getContent().stream()
                .filter(bt -> displayDates.contains(bt.getRelevantCreatedDate()))
                .map(BlockTicker::getTradeLog)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return detailResponseAssembler.assembleTagsAndTickersDetailResponse(result.getTargetDates(), blocks, tradeLogs);
    }
}
