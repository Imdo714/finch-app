package com.joojoo.api.user.domain.model.entity;

import com.joojoo.api.jwt.domain.model.entity.Token;
import com.joojoo.api.user.presentation.dto.request.kakao.KakaoUserDto;
import com.joojoo.api.user.domain.model.enums.Provider;
import com.joojoo.api.user.domain.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Token token;

    public static User createKakaoUserBuilder(KakaoUserDto kakaoUser, String socialRefreshToken) {
        return User.builder()
                .email(kakaoUser.getEmail())
                .name(kakaoUser.getName())
                .profileImageUrl(kakaoUser.getProfileImageUrl())
                .provider(Provider.KAKAO)
                .socialRefresh(socialRefreshToken)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateSocialRefreshToken(String newToken) {
        if (newToken != null && !newToken.isBlank()) {
            this.socialRefresh = newToken;
        }
    }
}
