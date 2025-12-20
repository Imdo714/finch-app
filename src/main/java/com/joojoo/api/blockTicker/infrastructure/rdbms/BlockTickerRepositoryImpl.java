package com.joojoo.api.blockTicker.infrastructure.rdbms;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTickerRepositoryImpl implements BlockTickerRepository {

    private final BlockTickerJpaRepository blockTickerJpaRepository;

    @Override
    public List<BlockTicker> saveAll(List<BlockTicker> blockTickers) {
        return blockTickerJpaRepository.saveAll(blockTickers);
    }
}
