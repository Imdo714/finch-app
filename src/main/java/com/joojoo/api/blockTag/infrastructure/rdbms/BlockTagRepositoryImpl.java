package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlockTagRepositoryImpl implements BlockTagRepository {

    private final BlockTagJpaRepository blockTagJpaRepository;


    @Override
    public List<BlockTag> saveAll(List<BlockTag> blockTags) {
        return blockTagJpaRepository.saveAll(blockTags);
    }
}
