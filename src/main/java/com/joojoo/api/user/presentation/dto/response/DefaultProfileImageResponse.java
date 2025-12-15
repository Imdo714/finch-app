package com.joojoo.api.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class DefaultProfileImageResponse {
    private List<ProfileImage> imageResponse;

    public static DefaultProfileImageResponse of(List<ProfileImage> defaultProfileImages) {
        return DefaultProfileImageResponse.builder()
                .imageResponse(defaultProfileImages)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class ProfileImage {
        private String name;
        private String imageUrl;
        private String description;
    }
}
