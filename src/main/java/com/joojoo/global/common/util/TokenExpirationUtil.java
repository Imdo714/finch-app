package com.joojoo.global.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TokenExpirationUtil {
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
