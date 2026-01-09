package com.joojoo.api.user.application.port.out.social;

import com.joojoo.api.user.domain.model.entity.User;

public interface SocialUnlink {
    void unlink(User user);
}
