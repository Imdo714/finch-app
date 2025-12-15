package com.joojoo.api.user.domain.model.entity;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.user.domain.model.enums.Currency;
import com.joojoo.api.user.domain.model.enums.Provider;
import com.joojoo.api.user.domain.model.enums.Role;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.global.common.entity.BaseTimeEntity;
import com.joojoo.global.exception.handleException.users.AdminOnlyAccessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(length = 50)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "social_refresh")
    private String socialRefresh;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 이거 양방향 할 필요가 없는거 같아서 기능 구현후 삭제 할 예정
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Token token;

    @Builder
    public User(String email, String name, String profileImageUrl, Provider provider, String providerId, Currency currency, String socialRefresh, Token token, Role role) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.currency = currency;
        this.socialRefresh = socialRefresh;
        this.role = role;
        this.token = token;
    }

    public static User createKakaoUserBuilder(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return User.builder()
            .email(kakaoUser.getEmail())
            .name(kakaoUser.getName())
            .profileImageUrl(kakaoUser.getProfileImageUrl())
            .provider(Provider.KAKAO)
            .currency(Currency.KRW)
            .role(Role.USER)
            .providerId(kakaoUser.getProviderId())
            .socialRefresh(socialRefreshToken)
            .build();
    }

    public static User createAppleUserBuilder(String providerId, String email, String appleRefreshToken) {
        return User.builder()
                .email(email)
                .provider(Provider.APPLE)
                .providerId(providerId)
                .socialRefresh(appleRefreshToken)
                .currency(Currency.KRW)
                .role(Role.USER)
                .build();
    }

    public void updateSocialRefreshToken(String newToken) {
        if (newToken != null && !newToken.isBlank()) {
            this.socialRefresh = newToken;
        }
    }

    public void withdraw() {
        this.email = null;
        this.name = null;
        this.profileImageUrl = null;
        this.delete();
        this.socialRefresh = null;
        this.providerId = null;
    }

    public void validateAdminPermission() {
        if (!Role.ADMIN.equals(this.role)) {
            throw new AdminOnlyAccessException();
        }
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateProfileImage(String fileName) {
        this.profileImageUrl = fileName;
    }
}
