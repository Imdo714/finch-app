package com.joojoo.api.blockTag.domain.repository;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.api.common.domain.response.detail.count.RelatedBlockDetailCountResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BlockTagRepository {
    List<BlockTag> saveAll(List<BlockTag> blockTags);

    List<BlockTag> findAllBlockTags(List<Long> blockIds);

    /** 블럭에서만 사용한 최근 태그 조회 */
    List<RecentTagsResponse.RecentTagsDto> findRecentTags(Long userId);

    /** userId가 tagId를 총 몇개의 블럭에 사용했는지 조회 */
    RelatedBlockDetailCountResponse getBlockCount(Long userId, Long tagId);

    /** 블랙태그 삭제 */
    void deleteByBlockIds(Long id);
    
    /** 2일치 날짜 조회해서 사용한 태그들 조회 */
    TagDateResult findAllByTagAndDate(Long userId, Long tagId, LocalDate targetDate);

    /** TradeLog에서 사용한 태그들 IN절 조회 */
    List<BlockTag> findAllTagsByTradeLogIds(List<Long> tradeLogIds);

    /** 사용자가 사용한 태그를 Redis에 저장 */
    void addTagsToRedis(Long userId, Set<String> lexEntries, Set<Long> tagIds);

    /** 사용자가 삭제한 태그를 Redis에 삭제 */
    void removeTagsFromRedis(Long userId, Long tagId, Set<String> lexEntries, int countToRemove);

    /** 삭제할 블럭아이디의 연관된 태그들 IN절 조회 */
    List<BlockTag> findAllByBlockIdIn(List<Long> blockIds);

    /** blockId와 연관된 태그들 조회 */
    List<BlockTag> findAllTagsByBlockId(Long blockId);

}
