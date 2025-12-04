package com.joojoo.api.user.domain.model.entity;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.user.domain.model.enums.Provider;
import com.joojoo.api.user.domain.model.enums.Status;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.global.common.entity.BaseTimeEntity;
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
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(length = 50)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "social_refresh")
    private String socialRefresh;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Token token;

    @Builder
    public User(String email, String name, String profileImageUrl, Provider provider, Status status, String socialRefresh, Token token) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.status = status;
        this.socialRefresh = socialRefresh;
        this.token = token;
    }

    public static User createKakaoUserBuilder(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return User.builder()
            .email(kakaoUser.getEmail())
            .name(kakaoUser.getName())
            .profileImageUrl(kakaoUser.getProfileImageUrl())
            .provider(Provider.KAKAO)
            .socialRefresh(socialRefreshToken)
            .status(Status.ACTIVE)
            .build();
    }

    public void updateSocialRefreshToken(String newToken) {
        if (newToken != null && !newToken.isBlank()) {
            this.socialRefresh = newToken;
        }
    }
}
