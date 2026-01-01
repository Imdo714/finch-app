package com.joojoo.api.tag.infrastructure.rdbms;

import com.joojoo.api.tag.domain.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
