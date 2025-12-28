package com.joojoo.api.util.detailQuery.dto;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class BlockRelatedDataBundle { // 데이터 전달 객체 역할
    private final Map<Long, List<BlockTag>> tagsByBlockId;
    private final Map<Long, List<BlockTicker>> tickersByBlockId;
    private final Map<Long, Long> childCounts;
    private final Map<Long, List<BlockTag>> tagsByTradeLogId;
    private final Map<Long, List<BlockTicker>> tickerByTradeLogId;

}
