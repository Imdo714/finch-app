package com.joojoo.api.blockTag.infrastructure.rdbms;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTagJpaRepository extends JpaRepository<BlockTag, Long> {


}
