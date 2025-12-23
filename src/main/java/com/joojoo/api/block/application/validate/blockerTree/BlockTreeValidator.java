package com.joojoo.api.block.application.validate.blockerTree;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;

import java.time.LocalDate;

public interface BlockTreeValidator {
    /** 트리 구조 검증 */
    void validateStructure(BlockSaveRequestDto requestDto);

    /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    LocalDate validateAndGetTargetDate(LocalDate lastDate);
}
