package com.joojoo.api.block.application;

import com.joojoo.api.block.application.port.in.DeleteBlockUseCase;
import com.joojoo.api.block.domain.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockUseCaseService implements DeleteBlockUseCase {

    private final BlockRepository blockRepository;

    @Override
    public void deleteAllBlockMappings(Long userId) {
        blockRepository.deleteAllBlockMappings(userId);
    }
}
