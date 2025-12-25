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

    List<Block> findRootBlocks(Long userId, Long lastId, int size);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount);

    LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult);

    List<Block> getBlocksByTagId(Long userId, Long tagId, Long lastBlockId, int limit);

    Optional<Block> findById(Long blockId);

    void delete(Block targetBlock);

    void deleteAllByIdInBatch(List<Long> idsToDelete);

    void updateChildrenParent(Block targetBlock, Block parentBlock);

    long countByParentIsNullAndUser(User user);

    void updateSequenceWithParent(User user, Block parent, int seq, int offset);

    void updateSequenceRoot(User user, int seq, int offset);

    Optional<Block> findByIdWithChildren(Long blockId);

    void decreaseDepthByIds(User user, List<Long> allDescendantIds);
}
