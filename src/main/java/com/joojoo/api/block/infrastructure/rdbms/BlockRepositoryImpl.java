package com.joojoo.api.block.infrastructure.rdbms;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockRepositoryImpl implements BlockRepository {

    private final BlockJpaRepository blockJpaRepository;

    @Override
    public Block save(Block block) {
        return blockJpaRepository.save(block);
    }

    @Override
    public void saveAll(List<Block> allBlocks) {
        blockJpaRepository.saveAll(allBlocks);
    }
}
