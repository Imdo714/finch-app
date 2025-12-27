package com.joojoo.api.tradeLog.presentation.controller;

import com.joojoo.api.tradeLog.application.TradeLogService;
import com.joojoo.api.tradeLog.presentation.dto.request.TradeRequestDto;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class TradeLogController {

    private final TradeLogService tradeLogService;

    @PostMapping
    public BaseResponse<String> createTradesLog(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody TradeRequestDto tradeRequestDto
    ) {
        tradeLogService.createTradesLog(user.getUserId(), tradeRequestDto);
        return BaseResponse.ok("템플릿 작성 성공!");
    }

}
