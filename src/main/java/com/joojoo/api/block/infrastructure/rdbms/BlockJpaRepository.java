package com.joojoo.api.block.infrastructure.rdbms;

import com.joojoo.api.block.domain.model.entity.Block;
import com.joojoo.api.user.domain.model.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockJpaRepository extends JpaRepository<Block, Long> {

    // 직계 자식들의 부모 변경
    @Modifying
    @Query("UPDATE Block b SET b.parent = :newParent WHERE b.parent = :oldParent")
    void updateChildrenParent(@Param("oldParent") Block oldParent, @Param("newParent") Block newParent);

    // 시퀀스 조정 (부모가 있을 때)
    @Modifying
    @Query("UPDATE Block b SET b.sequence = b.sequence + :offset " +
            "WHERE b.user = :user AND b.parent = :parent AND b.sequence > :seq")
    void updateSequenceWithParent(@Param("user") User user, @Param("parent") Block parent,@Param("seq") int seq, @Param("offset") int offset);

    // 시퀀스 조정 (최상위 블록일 때)
    @Modifying
    @Query("UPDATE Block b SET b.sequence = b.sequence + :offset " +
            "WHERE b.user = :user AND b.parent IS NULL AND b.sequence > :seq")
    void updateSequenceRoot(@Param("user") User user, @Param("seq") int seq, @Param("offset") int offset);

    @Query("SELECT b FROM Block b " +
            "LEFT JOIN FETCH b.children " +
            "WHERE b.id = :blockId")
    Optional<Block> findByIdWithChildren(Long blockId);

}
