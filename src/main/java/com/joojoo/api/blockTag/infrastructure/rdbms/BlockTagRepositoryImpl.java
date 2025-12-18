package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.repository.BlockTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BlockTagRepositoryImpl implements BlockTagRepository {

    private final BlockTagJpaRepository blockTagJpaRepository;


}
