package com.joojoo.api.block.application.service.command;

import com.joojoo.api.block.application.port.in.DeleteBlockUseCase;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.BlockDomainService;
import com.joojoo.api.block.domain.service.validation.BlockValidator;
import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import com.joojoo.api.metadata.application.port.out.MetadataPort;
import com.joojoo.global.exception.handleException.block.BlockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteBlockService implements DeleteBlockUseCase {

    private final BlockRepository blockRepository;
    private final BlockTagRepository blockTagRepository;
    private final BlockValidator blockValidator;
    private final MetadataPort metadataPort;
    private final BlockDomainService blockDomainService;

    @Override
    public void deleteAllBlockMappings(Long userId) {
        blockRepository.deleteAllBlockMappings(userId);
    }

    @Override
    @Transactional
    public void deleteBlock(Long userId, Long blockId, DeleteMode mode) {
        // 블록 조회 및 권한 검증
        Block targetBlock = blockRepository.findByIdWithChildren(blockId)
                .orElseThrow(BlockNotFoundException::new);
        blockValidator.validateOwner(targetBlock, userId);

        // 삭제 대상 ID 수집 및 관련 태그 백업 (Redis 삭제용)
        List<Long> idsToDelete = blockDomainService.getIdsToDelete(targetBlock, mode);
        List<BlockTag> tagsToRemove = blockTagRepository.findAllByBlockIdIn(idsToDelete);

        // 트리 구조 재조정 및 DB 삭제
        if (mode == DeleteMode.ALL) {
            blockDomainService.performRecursiveDelete(targetBlock, idsToDelete);
        } else {
            blockDomainService.performSingleDeleteWithPromotion(targetBlock);
        }
        // 태그는 Redis 차감
        if (!tagsToRemove.isEmpty()) {
            metadataPort.processRedisTagRemoval(userId, tagsToRemove);
        }
    }

}
