package com.joojoo.api.search.presentation.controller;

import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.search.application.SearchService;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import com.joojoo.api.ticker.presentation.dto.response.TickerSearchResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SEARCH API", description = "검색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    // 검색한 기록은 Redis에 저장하고
    // 태그 저장하는 부분에도 그냥 Redis에 모음 자음 분해서 넣어야 할듯

    @Operation(summary = "내가 사용한 태그 자동 검색 API", description = "검색창에서 내가 사용한 태그들 초성, 단어로 자동 검색해주는 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TagHistoryResponseDto.class))
            )
    })
    @GetMapping("/tags")
    public BaseResponse<TagHistoryResponseDto> searchStock(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String query
    ) {
        return BaseResponse.ok(searchService.searchTags(user.getUserId(), query));
    }

}
