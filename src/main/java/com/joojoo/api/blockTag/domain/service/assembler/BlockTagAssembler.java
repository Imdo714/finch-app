package com.joojoo.api.blockTag.domain.service.assembler;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.global.common.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;
import com.joojoo.global.common.assembler.detailApiAssembler.DetailResponseAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BlockTagAssembler {

    private final DetailResponseAssembler detailResponseAssembler;

    /** TagDateResult를 바탕으로 Response를 필터링하고 조립 */
    public DailyBlockDetailsResponse assembleBlockTagsResponse(TagDateResult result, List<LocalDate> displayDates) {
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

        return detailResponseAssembler.assembleTagsAndTickersDetailResponse(result.getTargetDates(), blocks, tradeLogs);
    }

}
