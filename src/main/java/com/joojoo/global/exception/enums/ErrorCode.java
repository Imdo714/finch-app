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

    // ───────────────────────────── 회원(users) ─────────────────────────────
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    USER_INACTIVE(403, "USER_INACTIVE", "비활성화된 회원입니다."),
    EMAIL_ALREADY_IN_USE(409, "USER_EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),

    // ───────────────────────────── 인증/인가(auth) ─────────────────────────────
    INVALID_AUTHORIZATION_CODE(400, "INVALID_AUTHORIZATION_CODE", "유효하지 않은 인가 코드입니다."),

    // ───────────────────────────── 소셜 로그인 (Kakao) ─────────────────────────────
    KAKAO_INVALID_ACCESS_TOKEN(401, "KAKAO_INVALID_ACCESS_TOKEN", "카카오 액세스 토큰이 유효하지 않습니다."),
    KAKAO_USER_INFO_RETRIEVE_FAILED(502, "KAKAO_USER_INFO_RETRIEVE_FAILED", "카카오 유저 정보 조회 서버에 문제가 발생했습니다."),
    KAKAO_TOKEN_ISSUE_FAILED(502, "KAKAO_TOKEN_ISSUE_FAILED", "카카오 토큰 발급 서버에 문제가 발생했습니다."),
    KAKAO_INVALID_TOKEN_RESPONSE(400, "KAKAO_INVALID_TOKEN_RESPONSE", "카카오 토큰 발급 응답이 올바르지 않습니다."),
    KAKAO_INVALID_USER_RESPONSE(400, "KAKAO_INVALID_USER_RESPONSE", "카카오 유저 정보를 불러오는데 실패했습니다."),
    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
