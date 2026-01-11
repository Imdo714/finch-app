package com.joojoo.api.metadata.application.port.out;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTicker.domain.model.entity.BlockTicker;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.domain.model.entity.Ticker;

import java.util.List;
import java.util.Map;

public interface MetadataPort {

    /** 티커, 태그 리스트 일괄 저장 */
    void saveTickersAndTags(List<BlockTicker> tickers, List<BlockTag> tags);

    /** 회원이 사용한 태그를 Redis에 저장 */
    void syncUserTagsToRedis(Long userId, Map<String, Tag> tagMap);

    /** 회원이 사용한 티커를 Redis에 저장 */
    void syncUserTickersToRedis(Long userId, Map<String, Ticker> tickerMap);

    /** 회원이 블럭에서 사용한 Tag 들을 Redis에서 삭제 */
    void processRedisTagRemoval(Long userId, List<BlockTag> oldTags);

    /** 회원이 블럭에서 사용한 Ticker 들을 Redis에서 삭제 */
    void processRedisTickerRemoval(Long userId, List<BlockTicker> oldTickers);

    /** BlockId 연관된 BlockTag, BlockTicker 삭제  */
    void deleteMetadataByBlockId(Long blockId);

}
