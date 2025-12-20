package com.joojoo.api.tag.infrastructure.queryDsl;

import com.joojoo.api.tag.domain.model.entity.Tag;

import java.util.List;
import java.util.Set;

public interface TagQueryDslRepository {
    List<Tag> findAllByNames(Set<String> names);
}
