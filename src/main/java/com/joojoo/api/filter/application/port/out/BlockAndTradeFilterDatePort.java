package com.joojoo.api.filter.application.port.out;

import com.joojoo.api.common.domain.enums.FilterCategory;

import java.time.LocalDate;
import java.util.List;

public interface BlockAndTradeFilterDatePort {
    /** 데이터가 존재하는 실제 날짜 리스트 추출 */
    List<LocalDate> findTargetDates(Long userId, List<Long> tagIds, List<Long> tickerIds,
                                    LocalDate lastDate, FilterCategory category, int limit
    );
}
