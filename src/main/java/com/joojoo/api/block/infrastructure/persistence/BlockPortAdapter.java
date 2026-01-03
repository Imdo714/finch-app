package com.joojoo.api.block.infrastructure.persistence;

import com.joojoo.api.block.application.port.out.BlockPort;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.infrastructure.rdbms.BlockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockPortAdapter implements BlockPort {

    private final BlockJpaRepository blockJpaRepository;

    @Override
    public void saveAll(List<Block> allBlocks) {
        blockJpaRepository.saveAll(allBlocks);
    }

}
