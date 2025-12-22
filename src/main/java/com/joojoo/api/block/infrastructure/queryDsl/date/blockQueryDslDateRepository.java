package com.joojoo.api.block.infrastructure.queryDsl.date;

import com.joojoo.api.block.domain.model.entity.Block;

import java.time.LocalDate;
import java.util.List;

public interface blockQueryDslDateRepository {
    List<Block> findBlocksByLatestDates(Long userId, LocalDate lastDate, int dateCount);

    LocalDate findNextAvailableDate(Long userId, LocalDate oldestDateInResult);
}
