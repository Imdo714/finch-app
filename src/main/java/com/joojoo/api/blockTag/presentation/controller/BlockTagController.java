package com.joojoo.api.blockTag.presentation.controller;

import com.joojoo.api.blockTag.application.BlockTagService;
import com.joojoo.api.blockTag.presentation.dto.response.RecentTagsResponse;
import com.joojoo.global.common.request.auth.CustomUserDetails;
import com.joojoo.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tag API", description = "Tag 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class BlockTagController {

    private final BlockTagService blockTagService;

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
        return BaseResponse.ok(blockTagService.getRecentTags(user.getUserId()));
    }

}
