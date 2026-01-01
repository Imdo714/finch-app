package com.joojoo.api.tag.domain.repository;

import com.joojoo.api.tag.domain.model.entity.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TagRepository {
    List<Tag> findAllByNames(Set<String> names);

    List<Tag> saveAll(List<Tag> newTags);

    Tag save(Tag tag);

    Optional<Tag> findById(Long tagId);
}
