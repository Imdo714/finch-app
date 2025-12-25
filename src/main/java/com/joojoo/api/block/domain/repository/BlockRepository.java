package com.joojoo.api.block.domain.repository;

import com.joojoo.api.block.domain.model.entity.Block;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BlockRepository {
    Block save(Block block);

    List<Block> saveAll(List<Block> allBlocks);

    List<Block> findAllChildrenByRootId(Long blockId);

    List<Block> findRootBlocks(Long userId, Long lastId, int size);

    Map<Long, Long> getChildCounts(List<Long> rootIds);

    List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount);

    LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult);

    List<Block> getBlocksByTagId(Long userId, Long tagId, Long lastBlockId, int limit);
}
