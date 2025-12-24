package com.joojoo.api.blockTag.presentation.dto.response;

import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockTagsResponse {
    private List<BlockDetailResponseDto> blocks;
    private boolean hasNext;
    private Long lastBlockId;

    public static BlockTagsResponse of(List<BlockDetailResponseDto> blocks, boolean hasNext, Long lastBlockId) {
        return new BlockTagsResponse(blocks, hasNext, lastBlockId);
    }
}
