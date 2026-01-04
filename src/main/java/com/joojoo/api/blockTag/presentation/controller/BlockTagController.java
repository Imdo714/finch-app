package com.joojoo.api.blockTag.presentation.controller;

import com.joojoo.api.blockTag.application.BlockTagService;
import com.joojoo.api.blockTag.application.port.in.GetBlockTagUseCase;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;
import com.joojoo.api.blockTag.presentation.dto.response.recent.RecentTagsResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import com.joojoo.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Tag API", description = "Tag 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class BlockTagController {

    private final BlockTagService blockTagService;

    private final GetBlockTagUseCase getBlockTagUseCase;

    @Operation(summary = "Tag 최근 사용 기록", description = "Tag 작성할때 최근 10개 기록 리스트 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RecentTagsResponse.class)
                    )
            )
    })
    @GetMapping("/recent")
    public BaseResponse<RecentTagsResponse> getRecentTags(
            @AuthenticationPrincipal CustomUserDetails user
    ){
        return BaseResponse.ok(getBlockTagUseCase.getRecentTags(user.getUserId()));
    }

    @Operation(summary = "Tag 상세 페이지 API", description = "내가 사용한 TagId로 블럭(노트) 리스트 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BlockTagsResponse.class)
                    )
            )
    })
    @GetMapping("/{tagId}")
    public BaseResponse<BlockTagsResponse> getBlockTagDetail(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tagId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ){
        return BaseResponse.ok(getBlockTagUseCase.getBlockTagDetail(user.getUserId(), tagId, lastDate));
    }

    @Operation(summary = "Tag 상세 페이지 블럭 개수 API", description = "태그 상세 페이지 위에 태그 이름 하고 블럭 수량을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = TotalCountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "태그를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{tagId}/count")
    public BaseResponse<TotalCountResponse> getBlockCount(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long tagId
    ){
        return BaseResponse.ok(blockTagService.getBlockCount(user.getUserId(), tagId));
    }

}
