package com.joojoo.api.block.application.detail;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;

import java.util.List;

public interface BlockDtoAssembler {
    // 상세 페이지용
    BlockDetailResponseDto assembleTree(Long rootId, List<Block> blocks, List<BlockTag> tags, List<BlockTicker> tickers);

}
