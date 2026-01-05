package com.joojoo.api.util.infrastructure.jaso;

import com.joojoo.api.util.port.in.JasoConverter;
import com.joojoo.global.util.hangul.HangulUtils;
import org.springframework.stereotype.Component;

@Component
public class HangulJasoAdapter implements JasoConverter {

    @Override
    public String convert(String text) {
        return HangulUtils.splitToJaso(text);
    }
}
