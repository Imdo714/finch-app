package com.joojoo.api.blockTag.infrastructure.redis;

import com.joojoo.api.common.search.range.SearchRange;

import java.util.Set;

public interface BlockTagRedisRepository {
    void addTagsToRedis(Long userId, Set<String> lexEntries, Set<Long> tagIds);

    void removeTagsFromRedis(Long userId, Long tagId, Set<String> lexEntries, int countToRemove);

    /** 내가 사용하고있는 태그들 검색 */
    Set<String> searchTagQuery(SearchRange range);

}
