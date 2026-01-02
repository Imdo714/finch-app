package com.joojoo.api.user.domain.repository;

import com.joojoo.api.user.domain.model.entity.User;

import java.util.Optional;

/** UserPort 역할 */
public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findByProviderId(String providerId);
    void delete(User user);
    boolean existsByName(String nickname);

    /** 유저 프록시 객체 반환 */
    User getReferenceById(Long userId);

    /** 유저가 없을 수도 있을 때 사용 */
    Optional<User> findById(Long userId);

    /** 유조가 반드시 있어야 할때 조회 */
    User getUserById(Long userId);
}
