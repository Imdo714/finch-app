package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.tradeLog.presentation.dto.response.chartOverlay.ChartOverlayResponse;
import com.joojoo.api.user.domain.model.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BlockRepository {
    Block save(Block block);

    List<Block> saveAll(List<Block> allBlocks);

    List<Block> findAllChildrenByRootId(Long blockId);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    /** 블럭이 있을 시, 없으면 BlockNotFoundException 예외 */
    Block getBlockById(Long blockId);

    void delete(Block targetBlock);

    void deleteAllByIdInBatch(List<Long> idsToDelete);

    void updateChildrenParent(Block targetBlock, Block parentBlock);

    void updateSequenceWithParent(User user, Block parent, int seq, int offset);

    void updateSequenceRoot(User user, int seq, int offset);

    Optional<Block> findByIdWithChildren(Long blockId);

    void deleteAllBlockMappings(Long userId);

    /** 사용자가 작성한 블럭에 포함된 티커 관련 정보 조회 */
    List<ChartOverlayResponse.AnalysisBlockDto> findByUserIdAndTickerId(Long userId, Long tickerId);
}
