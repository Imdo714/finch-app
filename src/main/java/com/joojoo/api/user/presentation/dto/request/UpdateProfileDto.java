package com.joojoo.api.user.presentation.dto.request;

import com.joojoo.api.user.domain.model.enums.DefaultProfileImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileDto {
    private String name;
    private DefaultProfileImage profile;
}
