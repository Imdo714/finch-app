package com.joojoo.api.block.presentation.controller;

import com.joojoo.api.block.application.BlockService;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Block API", description = "블럭(노트) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("block")
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "블럭(노트) 생성 API", description = "사용자가 작성한 블럭(노트) 트리 구조를 받아 저장합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(schema = @Schema(implementation = BlockResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 블럭 구조 (Ex. 루트 개수 오류, Depth 초과)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/create-blocks")
    public BaseResponse<BlockResponse> createBlock(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody BlockSaveRequestDto requestDto
    ) {
        return BaseResponse.ok(blockService.saveBlockTree(user.getUserId(), requestDto));
    }

}
