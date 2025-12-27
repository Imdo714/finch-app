package com.joojoo.api.blockTag.presentation.dto.response.all;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DailyGroupResponseDto {
    private LocalDate date;
    private List<BlockDetailMode> blocks;
    private List<TradeLogResponseDto> tradeLogs;

    public static DailyGroupResponseDto of(LocalDate date, Map<LocalDate, List<BlockDetailMode>> groupedBlocks, Map<LocalDate, List<TradeLogResponseDto>> groupedTradeLogs){
        return DailyGroupResponseDto.builder()
                .date(date)
                .blocks(groupedBlocks.getOrDefault(date, Collections.emptyList()))
                .tradeLogs(groupedTradeLogs.getOrDefault(date, Collections.emptyList()))
                .build();
    }


}
