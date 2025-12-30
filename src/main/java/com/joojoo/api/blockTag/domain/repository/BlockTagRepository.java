package com.joojoo.api.blockTag.domain.repository;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BlockTagRepository {
    List<BlockTag> saveAll(List<BlockTag> blockTags);

    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    /** 블럭에서만 사용한 최근 태그 조회 */
    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    BlockTagCountResponse getBlockCount(Long userId, Long tagId);

    void deleteByBlockIds(Long id);
    
    /** 2일치 날짜 조회해서 사용한 태그들 조회 */
    TagDateResult findAllByTagAndDate(Long userId, Long tagId, LocalDate targetDate);

    /** TradeLog에서 사용한 태그들 조회 */
    List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds);

    /** 사용자가 사용한 태그를 Redis에 저장 */
    void addTagsToRedis(Set<String> redisEntries);

    /** 사용자가 삭제한 태그를 Redis에 삭제 */
    void removeTagsFromRedis(Set<String> values);
}
