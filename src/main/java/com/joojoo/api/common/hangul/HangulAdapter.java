package com.joojoo.api.common.hangul;

import com.joojoo.global.util.hangul.HangulUtils;
import org.springframework.stereotype.Component;

@Component
public class HangulAdapter implements HangulConverter {

    @Override
    public String jasoConvert(String text) {
        return HangulUtils.splitToJaso(text);
    }

    @Override
    public String chosungConvert(String text) {
        return HangulUtils.getChosung(text);
    }
}
