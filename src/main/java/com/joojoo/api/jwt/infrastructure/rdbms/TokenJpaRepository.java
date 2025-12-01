package com.joojoo.api.jwt.infrastructure.rdbms;

import com.joojoo.api.jwt.domain.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenJpaRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByUser_UserId(Long userUserId);
}
