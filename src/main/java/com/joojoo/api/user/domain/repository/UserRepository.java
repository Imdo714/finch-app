package com.joojoo.api.user.domain.repository;

import com.joojoo.api.user.domain.model.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findByProviderId(String providerId);
    Optional<User> findById(Long userId);
    void delete(User user);
}
