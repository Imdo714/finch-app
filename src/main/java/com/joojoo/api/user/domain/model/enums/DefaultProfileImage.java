package com.joojoo.api.user.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultProfileImage {
    PROFILE_1("profile1", "기본 프로필1"),
    PROFILE_2("profile2", "기본 프로필2"),
    ;

    private final String fileName;
    private final String description;
}
