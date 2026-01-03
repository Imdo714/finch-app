package com.joojoo.api.blockTag.infrastructure.redis;

import java.util.Set;

public interface BlockTagRedisRepository {
    void addTagsToRedis(Long userId, Set<String> lexEntries, Set<Long> tagIds);

    void removeTagsFromRedis(Long userId, Long tagId, Set<String> lexEntries, int countToRemove);

    Set<String> searchTagQuery(String prefix);

}
