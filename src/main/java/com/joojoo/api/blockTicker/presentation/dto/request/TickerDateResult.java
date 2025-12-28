package com.joojoo.api.blockTicker.presentation.dto.request;

import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class TickerDateResult {
    private List<BlockTicker> content;
    private List<LocalDate> targetDates;
}
