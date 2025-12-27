package com.joojoo.api.blockTag.domain.repository;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.util.List;

public interface BlockTagRepository {
    List<BlockTag> saveAll(List<BlockTag> blockTags);

    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    /** 블럭에서만 사용한 최근 태그 조회 */
    List<RecentTagsResponse.RecentTagsDto> findBlockRecentTags(Long userId);

    /** 블럭, 템플릿에서 사용한 최근 태그 조회 */
    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    BlockTagCountResponse getBlockCount(Long userId, Long tagId);

    void deleteByBlockIds(Long id);
}
