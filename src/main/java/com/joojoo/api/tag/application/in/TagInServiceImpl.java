package com.joojoo.api.tag.application.in;

import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagInServiceImpl implements TagInService {

    private final TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Tag> getOrCreateTagMap(Set<String> names) {
        if (names.isEmpty()) return Collections.emptyMap();

        List<Tag> existing = tagRepository.findAllByNames(names);
        Map<String, Tag> tagMap = getTagMap(existing);
        List<Tag> newTags = getNewTags(names, tagMap);

        if (!newTags.isEmpty()) {
            tagRepository.saveAll(newTags)
                    .forEach(t -> tagMap.put(t.getName(), t));
        }
        return tagMap;
    }

    private static Map<String, Tag> getTagMap(List<Tag> existing) {
        return existing.stream()
                .collect(Collectors.toMap(Tag::getName, t -> t));
    }

    private static List<Tag> getNewTags(Set<String> names, Map<String, Tag> tagMap) {
        return names.stream()
                .filter(name -> !tagMap.containsKey(name))
                .map(name -> Tag.builder().name(name).build())
                .toList();
    }


}
