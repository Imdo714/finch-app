package com.joojoo.api.filter.presentation.controller;

import com.joojoo.api.common.domain.response.detail.DailyBlockDetailsResponse;
import com.joojoo.api.filter.application.FilterService;
import com.joojoo.api.filter.application.port.in.GetFilterUseCase;
import com.joojoo.api.filter.presentation.dto.request.FilterListDto;
import com.joojoo.api.filter.presentation.dto.request.TickerAndTagIdDto;
import com.joojoo.api.filter.presentation.dto.response.FilterCountResponse;
import com.joojoo.api.filter.presentation.dto.response.RelatedKeywordsResponse;
import com.joojoo.api.common.domain.request.auth.CustomUserDetails;
import com.joojoo.api.common.domain.response.BaseResponse;
import com.joojoo.api.common.domain.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Filter API", description = "상세 페이지 필터 관련 API")
@RestController
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;
    private final GetFilterUseCase getFilterUseCase;

    @Operation(summary = "필터 카테고리 적용 API", description = "필터를 적용한 노트, 템플릿을 보여주는 API입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DailyBlockDetailsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "태그 ID 또는 티커 ID 중 하나는 반드시 입력해야 합니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/filter")
    public BaseResponse<DailyBlockDetailsResponse> getFilterCategory(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody FilterListDto filterListDto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ){
        return BaseResponse.ok(getFilterUseCase.getFilterCategory(user.getUserId(), filterListDto, lastDate));
    }

    @Operation(summary = "필터 카테고리 적용 시 노트 개수 API", description = "필터 카테고리 적용 시 몇개의 노트가 있는지 개수를 알려주는 API 입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = FilterCountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "태그 ID 또는 티커 ID 중 하나는 반드시 입력해야 합니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/filter/count")
    public BaseResponse<FilterCountResponse> getFilterCategoryCount(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody FilterListDto filterListDto
    ){
        return BaseResponse.ok(filterService.getFilterCategoryCount(user.getUserId(), filterListDto));
    }

    @Operation(summary = "필터 연관 키워드 티커, 테그 API", description = "상세 종목의 연관된 키원드 티커, 태그 API 입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RelatedKeywordsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "태그 ID 또는 티커 ID 중 하나는 반드시 입력해야 합니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "태그 ID와 티커 ID는 동시에 입력할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/filter/relation")
    public BaseResponse<RelatedKeywordsResponse> getFilterRelation(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody TickerAndTagIdDto tickerAndTagIdDto
    ){
        return BaseResponse.ok(filterService.getFilterRelation(user.getUserId(), tickerAndTagIdDto));
    }

}
