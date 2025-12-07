package com.joojoo.api.jwt.infrastructure.rdbms;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.user.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenJpaRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);

    Optional<Token> findByUserId(Long userId);

    @Modifying
    @Query("delete from Token t where t.user.id = :userId")
    void deleteByUserId(Long userId);
}
