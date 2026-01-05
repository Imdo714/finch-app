package com.joojoo.global.util.hangul;

public class HangulUtils {

    // 초성 (19개)
    private static final char[] CHOSUNG = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };
    // 중성 (21개)
    private static final char[] JUNGSUNG = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    };
    // 종성 (28개)
    private static final char[] JONGSUNG = {
            '\0', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    /**
     * 문자열을 자모 단위로 완전 분해 (검색 & 저장용)
     * 예: "테슬라" -> "ㅌㅔㅅㅡㄹㄹㅏ"
     */
    public static String splitToJaso(String text) {
        if (text == null) return null;

        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) { // 한글인 경우
                int uniVal = ch - 0xAC00;
                int cho = uniVal / (21 * 28);
                int jung = (uniVal % (21 * 28)) / 28;
                int jong = uniVal % 28;

                sb.append(CHOSUNG[cho]);
                sb.append(JUNGSUNG[jung]);
                if (jong > 0) {
                    sb.append(JONGSUNG[jong]);
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 초성만 추출 (저장용) - [수정] 내용 채워넣음!
     * 예: "테슬라" -> "ㅌㅅㄹ"
     */
    public static String getChosung(String text) {
        if (text == null) return null;

        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                int uniVal = ch - 0xAC00;
                int cho = uniVal / (21 * 28);
                sb.append(CHOSUNG[cho]); // 초성만 append
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

}
