package com.joojoo.api.search.infrastructure.rdbms;

import com.joojoo.api.search.domain.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchJpaRepository extends JpaRepository<SearchHistory, Long> {

}
