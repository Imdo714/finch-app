package com.joojoo.api.block.presentation.dto.response.mainView;

import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BlockMainViewResponse {
    private List<DailyBlockGroupDto> dailyBlocks;
    private LocalDate nextCursorDate;

    public static BlockMainViewResponse of(List<BlockDetailResponseDto> dtos, LocalDate nextDate) {
        Map<LocalDate, List<BlockDetailResponseDto>> grouped = getDateListLinkedHashMap(dtos);

        return BlockMainViewResponse.builder()
                .dailyBlocks(
                        grouped.entrySet().stream()
                                .map(entry -> new DailyBlockGroupDto(entry.getKey(), entry.getValue()))
                                .toList()
                )
                .nextCursorDate(nextDate)
                .build();
    }

    private static LinkedHashMap<LocalDate, List<BlockDetailResponseDto>> getDateListLinkedHashMap(List<BlockDetailResponseDto> dtos) {
        return dtos.stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.getCreatedAt().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    @Getter
    @AllArgsConstructor
    public static class DailyBlockGroupDto {
        private LocalDate date;
        private List<BlockDetailResponseDto> blocks;
    }


}
