package com.joojoo.api.util.filter.controller;

import com.joojoo.api.blockTag.presentation.dto.request.TagListDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.util.filter.application.FilterService;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @PostMapping("/filter")
    public BaseResponse<BlockTagsResponse> getFilterCategory(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody TagListDto tagListDto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ){
        return BaseResponse.ok(filterService.getFilterCategory(user.getUserId(), tagListDto, lastDate));
    }
}
