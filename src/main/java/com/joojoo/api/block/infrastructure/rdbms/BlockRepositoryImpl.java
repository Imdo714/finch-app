package com.joojoo.api.block.infrastructure.rdbms;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.infrastructure.queryDsl.BlockQueryDslRepository;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepository {

    private final BlockJpaRepository blockJpaRepository;
    private final BlockQueryDslRepository blockQueryDslRepository;

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

}
