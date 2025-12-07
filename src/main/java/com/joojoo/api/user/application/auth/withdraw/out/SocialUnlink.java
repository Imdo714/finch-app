package com.joojoo.api.user.application.auth.withdraw.out;

import com.joojoo.api.user.domain.model.entity.User;

public interface SocialUnlink {
    void unlink(User user);
}
