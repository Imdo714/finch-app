package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.user.domain.model.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BlockRepository {
    Block save(Block block);

    List<Block> saveAll(List<Block> allBlocks);

    List<Block> findAllChildrenByRootId(Long blockId);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    /** 데이터가 있는 2일치 데이터 조회 */
    List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount);

    LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult);

    /** 블럭을 석택적으로 조회할 때 */
    Optional<Block> findById(Long blockId);

    /** 블럭이 있을 시, 없으면 BlockNotFoundException 예외 */
    Block getBlockById(Long blockId);

    void delete(Block targetBlock);

    void deleteAllByIdInBatch(List<Long> idsToDelete);

    void updateChildrenParent(Block targetBlock, Block parentBlock);

    void updateSequenceWithParent(User user, Block parent, int seq, int offset);

    void updateSequenceRoot(User user, int seq, int offset);

    Optional<Block> findByIdWithChildren(Long blockId);

    void deleteAllBlockMappings(Long userId);
}
