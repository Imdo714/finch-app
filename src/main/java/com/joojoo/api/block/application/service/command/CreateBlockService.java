package com.joojoo.api.block.application.service.command;

import com.joojoo.api.block.application.port.in.CreateBlockUseCase;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.domain.service.BlockDomainService;
import com.joojoo.api.block.domain.service.assembler.BlockTreeAssembler;
import com.joojoo.api.block.domain.service.validation.BlockValidator;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.metadata.application.port.in.MetadataUseCase;
import com.joojoo.api.user.application.port.in.GetUserUseCase;
import com.joojoo.api.user.domain.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateBlockService implements CreateBlockUseCase {

    private final BlockRepository blockRepository;
    private final GetUserUseCase getUserUseCase;
    private final BlockDomainService blockDomainService;
    private final BlockValidator blockValidator;
    private final BlockTreeAssembler blockTreeAssembler;
    private final MetadataUseCase metadataUseCase;

    @Override
    public BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto) {
        blockValidator.validateStructure(requestDto);
        User user = getUserUseCase.getUser(userId);

        List<Block> allBlocks = blockDomainService.createAndSaveBlocks(user, requestDto.getBlocks());
        blockRepository.saveAll(allBlocks); // TODO : JDBC Batch Insert 고려, 지금 블럭이 10개면 10개의 Insert 쿼리 작동 중

        metadataUseCase.processMetadata(allBlocks, user.getId());
        return blockTreeAssembler.assembleReconstructBlockTree(allBlocks);
    }

}
