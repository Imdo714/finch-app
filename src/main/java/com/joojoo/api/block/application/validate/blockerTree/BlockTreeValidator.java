package com.joojoo.api.block.application.validate.blockerTree;

import com.joojoo.api.block.domain.model.entity.Block;

import java.time.LocalDate;

public interface BlockTreeValidator {

    /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    LocalDate validateAndGetTargetDate(LocalDate lastDate);

    /** 자식들이 부모 레벨로 올라갔을 때 3개 제한을 넘지 않는지 검증 */
    void validatePromotionLimit(Block targetBlock, Block parentBlock);

    /** 현재 블럭의 작성자 여부 검증 */
    void validateOwner(Block targetBlock, Long userId);
}
