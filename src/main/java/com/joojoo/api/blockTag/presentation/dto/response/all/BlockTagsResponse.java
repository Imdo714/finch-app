package com.joojoo.api.blockTag.presentation.dto.response.all;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class BlockTagsResponse {
    private List<DailyGroupResponseDto> dailyGroups;
    private boolean hasNext;
    private LocalDate nextDate;

    public static BlockTagsResponse of(List<DailyGroupResponseDto> dailyGroups, boolean hasNext, LocalDate nextDate) {
        return new BlockTagsResponse(dailyGroups, hasNext, nextDate);
    }
}
