package com.joojoo.api.blockTag.infrastructure.queryDsl;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.util.List;

public interface BlockTagQueryDslRepository {
    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    RelatedBlockDetailCountResponse getBlockCount(Long userId, Long tagId);

    List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds);

    List<BlockTag> findAllByBlockIdIn(List<Long> blockIds);

    List<BlockTag> findAllTagsByBlockId(Long blockId);
}
