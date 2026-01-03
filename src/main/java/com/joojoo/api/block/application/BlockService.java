package com.joojoo.api.block.application;

import com.joojoo.api.block.domain.model.enums.DeleteMode;

public interface BlockService {

    void deleteBlock(Long userId, Long blockId, DeleteMode mode);
}
