package com.joojoo.api.block.application.port.in;

import com.joojoo.api.block.domain.model.enums.DeleteMode;

public interface DeleteBlockUseCase {
    void deleteBlock(Long userId, Long blockId, DeleteMode mode);
}
