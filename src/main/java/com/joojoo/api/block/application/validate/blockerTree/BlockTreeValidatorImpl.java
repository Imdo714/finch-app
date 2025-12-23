package com.joojoo.api.block.application.validate.blockerTree;

import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.global.exception.enums.ErrorCode;
import com.joojoo.global.exception.handleException.block.InvalidBlockStructureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BlockTreeValidatorImpl implements BlockTreeValidator {

    public static final int MAX_DEPTH = 2;

    @Override
    public void validateStructure(BlockSaveRequestDto requestDto) {
        if (requestDto.getBlocks() == null || requestDto.getBlocks().size() != 1) {
            throw new InvalidBlockStructureException(ErrorCode.INVALID_ROOT_BLOCK_COUNT);
        }

        validateDepth(requestDto.getBlocks().get(0), 0);
    }

    @Override /** 날짜가 없거나, 미래 날짜이면 오늘 날짜로 변경 */
    public LocalDate validateAndGetTargetDate(LocalDate lastDate) {
        if (lastDate == null || lastDate.isAfter(LocalDate.now())) {
            return LocalDate.now();
        }
        return lastDate;
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
}
