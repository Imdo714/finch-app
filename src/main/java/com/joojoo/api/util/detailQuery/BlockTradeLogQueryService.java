package com.joojoo.api.util.detailQuery;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.tradeLog.domain.model.entity.TradeLog;

import java.time.LocalDate;
import java.util.List;

public interface BlockTradeLogQueryService {
    BlockTagsResponse assembleBlockTagsResponse(List<LocalDate> targetDates, List<Block> blocks, List<TradeLog> tradeLogs);
}
