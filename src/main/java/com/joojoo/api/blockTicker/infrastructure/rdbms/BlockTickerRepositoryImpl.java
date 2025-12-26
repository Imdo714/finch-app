package com.joojoo.api.blockTicker.infrastructure.rdbms;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.infrastructure.queryDsl.BlockTickerQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTickerRepositoryImpl implements BlockTickerRepository {

    private final BlockTickerJpaRepository blockTickerJpaRepository;
    private final BlockTickerQueryDslRepository blockTickerQueryDslRepository;

    @Override
    public List<BlockTicker> saveAll(List<BlockTicker> blockTickers) {
        return blockTickerJpaRepository.saveAll(blockTickers);
    }

    @Override
    public List<BlockTicker> findAllBlockTickers(List<Long> blockIds) {
        return blockTickerQueryDslRepository.findAllBlockTickers(blockIds);
    }

    @Override
    public void deleteByBlockIds(Long id) {
        blockTickerJpaRepository.deleteByBlockIds(id);
    }
}
