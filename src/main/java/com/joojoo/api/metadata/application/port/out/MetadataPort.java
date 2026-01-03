package com.joojoo.api.metadata.application.port.out;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tag.domain.model.entity.Tag;

import java.util.List;
import java.util.Map;

public interface MetadataPort {

    /** 티커, 태그 리스트 일괄 저장 */
    void saveTickersAndTags(List<BlockTicker> tickers, List<BlockTag> tags);

    /** 회원이 사용한 태그를 Redis에 저장 */
    void syncUserTagsToRedis(Long userId, Map<String, Tag> tagMap);
}
