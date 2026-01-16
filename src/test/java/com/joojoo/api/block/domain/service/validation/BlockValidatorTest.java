package com.joojoo.api.block.domain.service.validation;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.global.exception.enums.ErrorCode;
import com.joojoo.global.exception.handleException.block.BlockOwnerMismatchException;
import com.joojoo.global.exception.handleException.block.BlockPromotionLimitExceededException;
import com.joojoo.global.exception.handleException.block.InvalidBlockStructureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlockValidatorTest {

    private final BlockValidator blockValidator = new BlockValidator();

    @Nested
    @DisplayName("validateStructure 테스트")
    class ValidateStructureTest {

        private BlockRequestDto createBlock(int depth, List<BlockRequestDto> children) {
            return BlockRequestDto.builder()
                    .depth(depth)
                    .children(children)
                    .content("테스트 블록")
                    .isSaved(false)
                    .build();
        }

        @Test
        @DisplayName("성공: 루트 블록이 1개이고 최대 깊이(2)를 넘지 않음")
        void success_validStructure() {
            // Given: Depth 0 -> Depth 1 -> Depth 2 구조
            BlockRequestDto depth2 = createBlock(2, null);
            BlockRequestDto depth1 = createBlock(1, List.of(depth2));
            BlockRequestDto root = createBlock(0, List.of(depth1));

            BlockSaveRequestDto request = new BlockSaveRequestDto(List.of(root));

            // When & Then
            assertDoesNotThrow(() -> blockValidator.validateStructure(request));
        }

        @Test
        @DisplayName("실패: 최대 깊이(2)를 초과하는 경우 (Depth 3 존재)")
        void fail_exceedMaxDepth() {
            // Given: Depth 0 -> 1 -> 2 -> 3
            // validator 로직상 currentDepth가 2일 때 자식이 있으면 예외가 발생합니다.
            BlockRequestDto depth3 = createBlock(3, null);
            BlockRequestDto depth2 = createBlock(2, List.of(depth3));
            BlockRequestDto depth1 = createBlock(1, List.of(depth2));
            BlockRequestDto root = createBlock(0, List.of(depth1));

            BlockSaveRequestDto request = new BlockSaveRequestDto(List.of(root));

            // When & Then
            InvalidBlockStructureException exception = assertThrows(InvalidBlockStructureException.class,
                    () -> blockValidator.validateStructure(request));

            assertEquals(ErrorCode.MAX_BLOCK_DEPTH_EXCEEDED, exception.getErrorCode());
        }

        @Test
        @DisplayName("실패: 루트 블록이 없거나 여러 개인 경우")
        void fail_invalidRootCount() {
            BlockSaveRequestDto emptyRequest = new BlockSaveRequestDto(List.of());
            assertThrows(InvalidBlockStructureException.class,
                    () -> blockValidator.validateStructure(emptyRequest));
        }
    }

    @Nested
    @DisplayName("validateOwner 테스트")
    class ValidateOwnerTest {

        @Test
        @DisplayName("성공: 블록 소유자와 요청자가 일치")
        void success_ownerMatch() {
            Long userId = 1L;
            Block block = mock(Block.class);
            User user = mock(User.class);

            when(block.getUser()).thenReturn(user);
            when(user.getId()).thenReturn(userId);

            assertDoesNotThrow(() -> blockValidator.validateOwner(block, userId));
        }

        @Test
        @DisplayName("실패: 블록 소유자가 다름")
        void fail_ownerMismatch() {
            Long userId = 1L;
            Long otherUserId = 2L;
            Block block = mock(Block.class);
            User user = mock(User.class);

            when(block.getUser()).thenReturn(user);
            when(user.getId()).thenReturn(otherUserId);

            assertThrows(BlockOwnerMismatchException.class,
                    () -> blockValidator.validateOwner(block, userId));
        }
    }

    @Nested
    @DisplayName("validatePromotionLimit 테스트")
    class ValidatePromotionLimitTest {

        @Test
        @DisplayName("성공: 자식이 승격되어도 부모의 자식 수가 3개 이하")
        void success_withinPromotionLimit() {
            // Given: 부모에게 2개의 자식이 있고(대상 포함), 대상 블록에 2개의 자식이 있는 경우
            // (2-1) + 2 = 3 (통과)
            Block parent = mock(Block.class);
            Block targetBlock = mock(Block.class);

            when(targetBlock.getDepth()).thenReturn(1);
            when(parent.getChildren()).thenReturn(List.of(targetBlock, mock(Block.class)));
            when(targetBlock.getChildren()).thenReturn(List.of(mock(Block.class), mock(Block.class)));

            assertDoesNotThrow(() -> blockValidator.validatePromotionLimit(targetBlock, parent));
        }

        @Test
        @DisplayName("실패: 승격 후 형제 수가 3개를 초과")
        void fail_exceedPromotionLimit() {
            // Given: 부모에게 2개의 자식이 있고, 대상 블록에 3개의 자식이 있는 경우
            // (2-1) + 3 = 4 (실패)
            Block parent = mock(Block.class);
            Block targetBlock = mock(Block.class);

            when(targetBlock.getDepth()).thenReturn(1);
            when(parent.getChildren()).thenReturn(List.of(targetBlock, mock(Block.class)));
            when(targetBlock.getChildren()).thenReturn(List.of(mock(Block.class), mock(Block.class), mock(Block.class)));

            assertThrows(BlockPromotionLimitExceededException.class,
                    () -> blockValidator.validatePromotionLimit(targetBlock, parent));
        }
    }

}