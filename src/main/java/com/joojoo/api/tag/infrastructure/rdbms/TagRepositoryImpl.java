package com.joojoo.api.tag.infrastructure.rdbms;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import com.joojoo.api.tag.infrastructure.queryDsl.TagQueryDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagJpaRepository tagJpaRepository;
    private final TagQueryDslRepository tagQueryDslRepository;

    @Override
    public List<Tag> findAllByNames(Set<String> names) {
        return tagQueryDslRepository.findAllByNames(names);
    }

    @Override
    public List<Tag> saveAll(List<Tag> newTags) {
         return tagJpaRepository.saveAll(newTags);
    }

    @Override
    public Tag save(Tag tag) {
        return tagJpaRepository.save(tag);
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        return tagJpaRepository.findById(tagId);
    }
}
