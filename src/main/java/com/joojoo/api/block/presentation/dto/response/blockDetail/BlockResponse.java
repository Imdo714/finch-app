package com.joojoo.api.block.presentation.dto.response.blockDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BlockResponse {
    private List<BlockResponseDto> blockList;

    public static BlockResponse of(List<BlockResponseDto> blocks) {
        return new BlockResponse(blocks);
    }
}
