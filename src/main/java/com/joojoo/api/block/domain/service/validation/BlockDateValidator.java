package com.joojoo.api.block.domain.service.validation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BlockDateValidator {

    /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    public LocalDate validateAndGetTargetDate(LocalDate lastDate) {
        if (lastDate == null || lastDate.isAfter(LocalDate.now())) {
            return LocalDate.now();
        }
        return lastDate;
    }
}
