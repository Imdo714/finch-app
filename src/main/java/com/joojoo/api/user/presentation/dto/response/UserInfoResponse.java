package com.joojoo.api.user.presentation.dto.response;

import com.joojoo.api.user.domain.model.entity.User;
import com.joojoo.api.user.domain.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String name;
    private String profileUrl;
    private Currency currency;

    public static UserInfoResponse of(User user, String fullProfileUrl){
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileUrl(fullProfileUrl)
                .currency(user.getCurrency())
                .build();
    }

    public static UserInfoResponse of(User user){
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileUrl(null)
                .currency(user.getCurrency())
                .build();
    }
}
