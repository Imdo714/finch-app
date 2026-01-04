package com.joojoo.api.block.domain.service;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.validation.BlockValidator;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.user.domain.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BlockDomainService {

    private final BlockRepository blockRepository;
    private final BlockValidator blockValidator;

    /** 리스트에 블럭을 담아 한번에 저장하는 메서드 */
    public List<Block> createAndSaveBlocks(User user, List<BlockRequestDto> dtos) {
        List<Block> allBlocks = new ArrayList<>();
        blocksRecursive(user, null, dtos, allBlocks);
        return allBlocks;
    }

    /** RequestDto를 순회하며 JPA 엔티티(Block)를 생성하고, allBlocks에 수집하는 메서드 */
    public void blocksRecursive(User user, Block parentBlock, List<BlockRequestDto> blockDtos, List<Block> allBlocks) {
        if (blockDtos == null || blockDtos.isEmpty()) { return; }

        for (BlockRequestDto dto : blockDtos) {
            Block block = Block.createBlockBuild(user, parentBlock, dto);
            allBlocks.add(block);
            blocksRecursive(user, block, dto.getChildren(), allBlocks); // 자식 재귀 호출
        }
    }

    /** 삭제할 블럭ID 추출 */
    public List<Long> getIdsToDelete(Block targetBlock, DeleteMode mode) {
        if (mode == DeleteMode.ALL) {
            List<Long> ids = new ArrayList<>();
            targetBlock.collectAllIds(ids);
            return ids;
        }
        return List.of(targetBlock.getId());
    }

    /** 단일 블록 삭제 및 자식 노드 승격 로직 */
    public void performSingleDeleteWithPromotion(Block targetBlock) {
        Block parent = targetBlock.getParent();
        List<Block> children = targetBlock.getChildren();
        blockValidator.validatePromotionLimit(targetBlock, parent);

        int offset = children.size() - 1;
        shiftSiblings(targetBlock, offset); // 내부 private 메서드로 유지

        blockRepository.updateChildrenParent(targetBlock, parent);

        int startSeq = targetBlock.getSequence();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).promote(parent, startSeq + i);
        }

        targetBlock.disconnectChildren();
        blockRepository.delete(targetBlock);
    }

    /** 재귀 삭제 로직 */
    public void performRecursiveDelete(Block targetBlock, List<Long> idsToDelete) {
        shiftSiblings(targetBlock, -1);
        blockRepository.deleteAllByIdInBatch(idsToDelete);
    }

    /** 시퀀스 조정 메서드 */
    private void shiftSiblings(Block targetBlock, int offset) {
        if (offset == 0) return;
        if (targetBlock.getParent() != null) {
            blockRepository.updateSequenceWithParent(targetBlock.getUser(), targetBlock.getParent(), targetBlock.getSequence(), offset);
        } else {
            blockRepository.updateSequenceRoot(targetBlock.getUser(), targetBlock.getSequence(), offset);
        }
    }

}
