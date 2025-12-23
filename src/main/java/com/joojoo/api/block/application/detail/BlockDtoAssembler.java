package com.joojoo.api.block.application.detail;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;

import java.util.List;
import java.util.Map;

public interface BlockDtoAssembler {
    // 상세 페이지용
    BlockDetailResponseDto assembleTree(Long rootId, List<Block> blocks, List<BlockTag> tags, List<BlockTicker> tickers);

    // 메인 페이지용
    List<BlockDetailResponseDto> assembleMainList(List<Block> blocks, List<BlockTag> tags, List<BlockTicker> tickers, Map<Long, Long> childCounts);

    // 리스트형식에서 응답 형식인 트리구조로 변환
    BlockResponse assembleReconstructBlockTree(List<Block> allBlocks);
}
