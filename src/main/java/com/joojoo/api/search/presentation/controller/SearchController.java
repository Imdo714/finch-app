package com.joojoo.api.search.presentation.controller;

import com.joojoo.api.common.domain.request.auth.CustomUserDetails;
import com.joojoo.api.common.domain.response.BaseResponse;
import com.joojoo.api.common.domain.response.ErrorResponse;
import com.joojoo.api.search.application.port.in.CreateSearchUseCase;
import com.joojoo.api.search.application.port.in.GetSearchUseCase;
import com.joojoo.api.search.presentation.dto.request.SearchRequestDto;
import com.joojoo.api.search.presentation.dto.response.RecentSearchListResponse;
import com.joojoo.api.search.presentation.dto.response.TagHistoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SEARCH API", description = "검색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final GetSearchUseCase getSearchUseCase;
    private final CreateSearchUseCase createSearchUseCase;

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
        return BaseResponse.ok(getSearchUseCase.searchTags(user.getUserId(), query));
    }

    @Operation(summary = "최근 검색 저장 API", description = "검색한 ID를 최근 검색어에 저장하는 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "최근 검색어 저장 성공!",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원, 티커, 태그를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/record")
    public BaseResponse<String> recordSearch(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody SearchRequestDto requestDto
    ) {
        createSearchUseCase.recordSearch(user.getUserId(), requestDto);
        return BaseResponse.ok("최근 검색어 저장 성공!");
    }

    @Operation(summary = "최근 검색어 리스트 조회 API", description = "최근 검색했던 내역 조회 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecentSearchListResponse.class))
            )
    })
    @GetMapping("/record")
    public BaseResponse<RecentSearchListResponse> recordSearchList(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return BaseResponse.ok(getSearchUseCase.recordSearchList(user.getUserId()));
    }

}
