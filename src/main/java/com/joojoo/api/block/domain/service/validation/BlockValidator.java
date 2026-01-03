package com.joojoo.api.block.domain.service.validation;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.global.exception.enums.ErrorCode;
import com.joojoo.global.exception.handleException.block.BlockOwnerMismatchException;
import com.joojoo.global.exception.handleException.block.InvalidBlockStructureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlockValidator {

    public static final int MAX_DEPTH = 2;

    /** 트리 구조 검증 */
    public void validateStructure(BlockSaveRequestDto requestDto) {
        if (requestDto.getBlocks() == null || requestDto.getBlocks().size() != 1) {
            throw new InvalidBlockStructureException(ErrorCode.INVALID_ROOT_BLOCK_COUNT);
        }

        validateDepth(requestDto.getBlocks().get(0), 0);
    }

    private void validateDepth(BlockRequestDto block, int currentDepth) {
        if (currentDepth == MAX_DEPTH) {
            if (block.getChildren() != null && !block.getChildren().isEmpty()) {
                throw new InvalidBlockStructureException(ErrorCode.MAX_BLOCK_DEPTH_EXCEEDED);
            }
            return;
        }

        if (currentDepth > MAX_DEPTH) {
            throw new InvalidBlockStructureException(ErrorCode.MAX_BLOCK_DEPTH_EXCEEDED);
        }

        if (block.getChildren() != null) {
            for (BlockRequestDto child : block.getChildren()) {
                validateDepth(child, currentDepth + 1);
            }
        }
    }

    /** 현재 블럭의 작성자 여부 검증 */
    public void validateOwner(Block targetBlock, Long userId) {
        if (!targetBlock.getUser().getId().equals(userId)) {
            throw new BlockOwnerMismatchException();
        }
    }
}
