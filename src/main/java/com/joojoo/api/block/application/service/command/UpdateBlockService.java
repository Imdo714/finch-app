package com.joojoo.api.block.application.service.command;

import com.joojoo.api.block.application.port.in.UpdateBlockUseCase;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.validation.BlockValidator;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;
import com.joojoo.api.metadata.application.port.in.MetadataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateBlockService implements UpdateBlockUseCase {

    private final BlockRepository blockRepository;
    private final BlockValidator blockValidator;
    private final MetadataUseCase metadataUseCase;

    @Override
    @Transactional
    public void updateBlock(Long userId, Long blockId, BlockUpdateDto blockUpdateDto) {
        Block targetBlock = blockRepository.getBlockById(blockId);
        blockValidator.validateOwner(targetBlock, userId);

        targetBlock.updateContent(blockUpdateDto.getContent());
        metadataUseCase.processMetadata(targetBlock, userId);
    }
}
