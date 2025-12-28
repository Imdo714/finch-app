package com.joojoo.api.blockTag.infrastructure.queryDsl;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.util.List;

public interface BlockTagQueryDslRepository {
    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    BlockTagCountResponse getBlockCount(Long userId, Long tagId);

    List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds);
}
