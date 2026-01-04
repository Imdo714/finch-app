package com.joojoo.api.block.application.validate.blockerTree;

import java.time.LocalDate;

public interface BlockTreeValidator {

    /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    LocalDate validateAndGetTargetDate(LocalDate lastDate);

}
