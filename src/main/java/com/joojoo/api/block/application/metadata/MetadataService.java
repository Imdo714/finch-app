package com.joojoo.api.block.application.metadata;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.metadata.MatchedMetadataDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MetadataService {
    Map<Block, List<MatchedMetadataDto>> scanBlocks(List<Block> allBlocks, Set<String> tickerNames, Set<String> tagNames);

    void mapToEntities(Block block, List<MatchedMetadataDto> matches, Map<String, Ticker> tickerMap, Map<String, Tag> tagMap, List<BlockTicker> blockTickers, List<BlockTag> blockTags);

    void processMetadata(List<Block> allBlocks);
}
