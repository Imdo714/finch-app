package com.joojoo.global.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 전역 에러 코드 관리 Enum
 * - 예외 발생 시 클라이언트에 전달할 응답 형식 정의
 * - HTTP 상태 코드, 에러 코드 문자열, 메시지를 포함
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ───────────────────────────── 공통/인프라 ─────────────────────────────
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
    INVALID_TOKEN(422, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(401, "EXPIRED_ACCESS_TOKEN", "Access 토큰이 만료되었습니다."),
    EXPIRED_REFRESH_TOKEN(401, "EXPIRED_REFRESH_TOKEN", "Refresh 토큰이 만료되었습니다."),
    REDIS_CONNECTION_FAIL(401, "REDIS_CONNECTION_FAIL", "Redis 연결에 실패하였습니다."),

    // ───────────────────────────── 회원(users) ─────────────────────────────
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    USER_INACTIVE(403, "USER_INACTIVE", "비활성화된 회원입니다."),
    EMAIL_ALREADY_IN_USE(409, "USER_EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),
    USER_MISMATCH(403, "USER_MISMATCH", "회원 정보가 일치하지 않습니다."),
    ADMIN_MISMATCH(403, "ADMIN_MISMATCH", "관리자만 접근할 수 있습니다."),

    // ───────────────────────────── 인증/인가(auth) ─────────────────────────────
    INVALID_AUTHORIZATION_CODE(400, "INVALID_AUTHORIZATION_CODE", "유효하지 않은 인가 코드입니다."),

    // ───────────────────────────── 소셜 로그인 (Kakao) ─────────────────────────────
    KAKAO_INVALID_ACCESS_TOKEN(401, "KAKAO_INVALID_ACCESS_TOKEN", "카카오 액세스 토큰이 유효하지 않습니다."),
    KAKAO_USER_INFO_RETRIEVE_FAILED(502, "KAKAO_USER_INFO_RETRIEVE_FAILED", "카카오 유저 정보 조회 서버에 문제가 발생했습니다."),
    KAKAO_TOKEN_ISSUE_FAILED(502, "KAKAO_TOKEN_ISSUE_FAILED", "카카오 토큰 발급 서버에 문제가 발생했습니다."),
    KAKAO_INVALID_TOKEN_RESPONSE(400, "KAKAO_INVALID_TOKEN_RESPONSE", "카카오 토큰 발급 응답이 올바르지 않습니다."),
    KAKAO_INVALID_USER_RESPONSE(400, "KAKAO_INVALID_USER_RESPONSE", "카카오 유저 정보를 불러오는데 실패했습니다."),

    // ---------------------------- 소셜로그인 (Apple) ----------------------------
    APPLE_TOKEN_ISSUE_FAILED(502, "APPLE_TOKEN_ISSUE_FAILED", "애플 토큰 발급 서버에 문제가 발생했습니다."),
    APPLE_INVALID_TOKEN_RESPONSE(400, "APPLE_INVALID_TOKEN_RESPONSE", "애플 토큰 발급 응답이 올바르지 않습니다."),
    APPLE_INVALID_ID_TOKEN(400, "APPLE_INVALID_ID_TOKEN", "애플 ID Token 형식이 올바르지 않거나 파싱에 실패했습니다."),

    // ---------------------------- 티커 (Ticker) ----------------------------
    INVALID_TICKER_NAME(401, "INVALID_TICKER_NAME", "티커 또는 이름을 작성하세요."),

    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
