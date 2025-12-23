package com.joojoo.api.block.application.metadata;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MetadataService {
    Map<Block, List<MatchedMetadataDto>> scanBlocks(List<Block> allBlocks, Set<String> tickerNames, Set<String> tagNames);

    void processMetadata(List<Block> allBlocks, Long userId);
}
