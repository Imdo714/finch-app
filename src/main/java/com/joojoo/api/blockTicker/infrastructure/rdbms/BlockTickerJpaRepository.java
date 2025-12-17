package com.joojoo.api.blockTicker.infrastructure.rdbms;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTickerJpaRepository extends JpaRepository<BlockTicker, Long> {
}
