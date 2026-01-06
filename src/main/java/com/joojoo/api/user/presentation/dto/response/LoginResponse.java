package com.joojoo.api.user.presentation.dto.response;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private Role role;
    private String email;
    private String name;
    private String profileImageUrl;
    private String accessToken;
    private String refreshToken;

    public static LoginResponse of(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .id(user.getId())
                .role(user.getRole())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
