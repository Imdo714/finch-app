package com.joojoo.api.blockTag.presentation.dto.response.detail;

import com.joojoo.api.blockTag.presentation.dto.response.all.DailyGroupResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyBlockDetailsResponse { // TODO : 이거 클래스명 수정
    private List<DailyGroupResponseDto> groups;
    private boolean hasNext;
    private LocalDate nextDate;

    public static DailyBlockDetailsResponse of(List<DailyGroupResponseDto> blocks, boolean hasNext, LocalDate nextDate) {
        return new DailyBlockDetailsResponse(blocks, hasNext, nextDate);
    }
}
