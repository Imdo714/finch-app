package com.joojoo.api.blockTag.infrastructure.redis;

import java.util.Set;

public interface BlockTagRedisRepository {
    void addTagsToRedis(Set<String> values);

    void removeTagsFromRedis(Set<String> values);
}
