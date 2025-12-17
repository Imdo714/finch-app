package com.joojoo.api.blockTicker.infrastructure.rdbms;

import com.joojoo.api.blockTicker.domain.repository.BlockTickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlockTickerRepositoryImpl implements BlockTickerRepository {

    private final BlockTickerJpaRepository blockTickerJpaRepository;
}
