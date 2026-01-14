package com.joojoo.api.block.infrastructure.queryDsl;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;

import java.util.List;
import java.util.Map;

public interface BlockQueryDslRepository {
    List<Block> findAllChildrenByRootId(Long blockId);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    void deleteAllBlockMappings(Long userId);

    List<ChartOverlayResponse.AnalysisBlockDto> findByUserIdAndTickerId(Long userId, Long tickerId);
}
