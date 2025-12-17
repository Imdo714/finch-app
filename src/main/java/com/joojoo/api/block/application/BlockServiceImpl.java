package com.joojoo.api.block.application;

import com.joojoo.api.block.application.validate.blockerTree.BlockTreeValidator;
import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.block.domain.repository.BlockRepository;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockRequestDto;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponseDto;
import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.repository.UserRepository;
import com.joojoo.global.exception.handleException.users.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;
    private final BlockTreeValidator blockTreeValidator;

    @Override
    @Transactional
    public BlockResponse saveBlockTree(Long userId, BlockSaveRequestDto requestDto) {
        blockTreeValidator.validateStructure(requestDto);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<BlockResponseDto> savedTree = saveBlocksRecursive(user, null, requestDto.getBlocks());
        return BlockResponse.of(savedTree);
    }

    private List<BlockResponseDto> saveBlocksRecursive(User user, Block parentBlock, List<BlockRequestDto> blockDtos) {
        if (blockDtos == null || blockDtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<BlockResponseDto> responseList = new ArrayList<>();

        for (BlockRequestDto dto : blockDtos) {
            Block block = Block.createBlockBuild(user, parentBlock, dto);
            Block savedBlock = blockRepository.save(block);

            // 자식 재귀 호출, 없으면 빈 List 반환
            List<BlockResponseDto> childrenResponse = saveBlocksRecursive(user, savedBlock, dto.getChildren());
            BlockResponseDto responseDto = BlockResponseDto.of(savedBlock, childrenResponse);
            responseList.add(responseDto);
        }
        return responseList;
    }

}
