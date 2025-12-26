package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTagJpaRepository extends JpaRepository<BlockTag, Long> {

    @Modifying
    @Query("delete FROM BlockTag bt where bt.block.id = :id")
    void deleteByBlockIds(Long id);
}
