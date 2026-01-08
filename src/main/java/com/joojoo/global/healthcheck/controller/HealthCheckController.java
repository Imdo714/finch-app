package com.joojoo.global.healthcheck.controller;

import com.joojoo.api.common.domain.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "헬스 체크 API", description = "헬스 체크 API")
@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @Operation(summary = "Spring Boot 서버 헬스 체크", description = "Spring Boot 서버 상태를 확인합니다")
    @GetMapping("/health")
    public BaseResponse<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "서비스 정상 동작");

        return BaseResponse.ok(response);
    }
}
