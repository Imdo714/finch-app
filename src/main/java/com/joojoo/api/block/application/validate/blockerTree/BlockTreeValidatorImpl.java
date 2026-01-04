package com.joojoo.api.block.application.validate.blockerTree;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BlockTreeValidatorImpl implements BlockTreeValidator {

    public static final int MAX_DEPTH = 2;

    @Override /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    public LocalDate validateAndGetTargetDate(LocalDate lastDate) {
        if (lastDate == null || lastDate.isAfter(LocalDate.now())) {
            return LocalDate.now();
        }
        return lastDate;
    }

}
