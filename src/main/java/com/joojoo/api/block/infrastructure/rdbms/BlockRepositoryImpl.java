package com.joojoo.api.block.infrastructure.rdbms;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.infrastructure.queryDsl.BlockQueryDslRepository;
import com.joojoo.api.block.infrastructure.queryDsl.date.blockQueryDslDateRepository;
import com.joojoo.api.user.domain.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepository {

    private final BlockJpaRepository blockJpaRepository;
    private final BlockQueryDslRepository blockQueryDslRepository;
    private final blockQueryDslDateRepository blockQueryDslDateRepository;

    @Override
    public Block save(Block block) {
        return blockJpaRepository.save(block);
    }

    @Override
    public List<Block> saveAll(List<Block> allBlocks) {
        return blockJpaRepository.saveAll(allBlocks);
    }

    @Override
    public List<Block> findAllChildrenByRootId(Long blockId) {
        return blockQueryDslRepository.findAllChildrenByRootId(blockId);
    }

    @Override
    public List<Block> findRootBlocks(Long userId, Long lastId, int size) {
        return blockQueryDslRepository.findRootBlocks(userId, lastId, size);
    }

    @Override
    public Map<Long, Long> getChildCounts(List<Long> rootIds) {
        return blockQueryDslRepository.getChildCounts(rootIds);
    }

    @Override
    public List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount) {
        return blockQueryDslDateRepository.findBlocksByLatestDates(userId, lastDate, dateCount);
    }

    @Override
    public LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult) {
        return blockQueryDslDateRepository.findNextAvailableDate(userId, oldestDateInResult);
    }

    @Override
    public List<Block> getBlocksByTagId(Long userId, Long tagId, Long lastBlockId, int limit) {
        return blockQueryDslRepository.getBlocksByTagId(userId, tagId, lastBlockId, limit);
    }

    @Override
    public Optional<Block> findById(Long blockId) {
        return blockJpaRepository.findById(blockId);
    }

    @Override
    public void delete(Block targetBlock) {
        blockJpaRepository.delete(targetBlock);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> idsToDelete) {
        blockJpaRepository.deleteAllByIdInBatch(idsToDelete);
    }

    @Override
    public void updateChildrenParent(Block targetBlock, Block parentBlock) {
        blockJpaRepository.updateChildrenParent(targetBlock, parentBlock);
    }

    @Override
    public void updateSequenceWithParent(User user, Block parent, int seq, int offset) {
        blockJpaRepository.updateSequenceWithParent(user, parent, seq, offset);
    }

    @Override
    public void updateSequenceRoot(User user, int seq, int offset) {
        blockJpaRepository.updateSequenceRoot(user, seq, offset);
    }

    @Override
    public Optional<Block> findByIdWithChildren(Long blockId) {
        return blockJpaRepository.findByIdWithChildren(blockId);
    }


}
