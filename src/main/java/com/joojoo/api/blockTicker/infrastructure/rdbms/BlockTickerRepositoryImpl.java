package com.joojoo.api.blockTicker.infrastructure.rdbms;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import com.joojoo.api.blockTicker.infrastructure.queryDsl.BlockTickerQueryDslRepository;
import com.joojoo.api.blockTicker.infrastructure.queryDsl.date.BlockTickerDateQueryDslRepository;
import com.joojoo.api.blockTicker.presentation.dto.request.TickerDateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTickerRepositoryImpl implements BlockTickerRepository {

    private final BlockTickerJpaRepository blockTickerJpaRepository;
    private final BlockTickerQueryDslRepository blockTickerQueryDslRepository;
    private final BlockTickerDateQueryDslRepository blockTickerDateQueryDslRepository;

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

    @Override
    public TickerDateResult findAllByTickerAndDate(Long userId, Long tickerId, LocalDate targetDate) {
        return blockTickerDateQueryDslRepository.findAllByTickerAndDate(userId, tickerId, targetDate);
    }

    @Override
    public List<BlockTicker> findAllTickersByTradeLogIds(List<Long> tradeLogIds) {
        return blockTickerQueryDslRepository.findAllTickersByTradeLogIds(tradeLogIds);
    }

    @Override
    public List<BlockTicker> findAllTickersByBlockId(Long blockId) {
        return blockTickerQueryDslRepository.findAllTickersByBlockId(blockId);
    }
}
