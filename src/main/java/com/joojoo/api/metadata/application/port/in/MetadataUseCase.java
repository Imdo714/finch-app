package com.joojoo.api.metadata.application.port.in;

import com.joojoo.api.block.domain.model.entity.Block;

import java.util.List;

public interface MetadataUseCase {
    void processMetadata(List<Block> allBlocks, Long userId);
}
