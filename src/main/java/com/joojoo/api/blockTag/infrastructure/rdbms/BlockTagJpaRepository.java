package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockTagJpaRepository extends JpaRepository<BlockTag, Long> {

    @Modifying
    @Query("delete FROM BlockTag bt where bt.block.id = :id")
    void deleteByBlockIds(Long id);

    @Query(value =
            "SELECT t.id AS id, t.name AS name FROM (" +
                    "  (SELECT tag_id, created_at FROM block_tags WHERE user_id = :userId) " +
                    "  UNION " +
                    "  (SELECT tag_id, created_at FROM trade_log_tags WHERE user_id = :userId) " +
                    ") AS combined_tags " +
                    "JOIN tags t ON t.id = combined_tags.tag_id " +
                    "ORDER BY combined_tags.created_at DESC " +
                    "LIMIT 10", nativeQuery = true)
    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);
}
