package com.joojoo.api.block.domain.service.date;

import com.joojoo.api.block.application.port.out.LoadDailyDetailsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BlockDatePolicy {

    public static final int FETCH_DATE_COUNT = 3;
    public static final int DISPLAY_DATE_COUNT = 2;

    private final LoadDailyDetailsPort loadDailyDetailsPort;

    public List<LocalDate> finalTargetDates(Long userId, LocalDate targetDate){
        // block 날짜 조회
        List<LocalDate> blockDates = loadDailyDetailsPort.getTargetBlockDates(userId, targetDate, FETCH_DATE_COUNT);
        // TradeLog 날짜 조회
        List<LocalDate> tradeLogDates = loadDailyDetailsPort.getTradeLogDates(userId, targetDate, FETCH_DATE_COUNT);
        return determineFinalDates(blockDates, tradeLogDates);
    }

    public List<LocalDate> determineFinalDates(List<LocalDate> blockDates, List<LocalDate> logDates) {
        return Stream.concat(blockDates.stream(), logDates.stream())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(FETCH_DATE_COUNT)
                .toList();
    }

    public List<LocalDate> extractDisplayDates(List<LocalDate> allDates) {
        return allDates.stream().limit(DISPLAY_DATE_COUNT).toList();
    }

}
