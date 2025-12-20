package com.joojoo.api.tag.application.in;

import com.joojoo.api.tag.domain.model.entity.Tag;

import java.util.Map;
import java.util.Set;

public interface TagInService {
    Map<String, Tag> getOrCreateTagMap(Set<String> tagNames);
}
