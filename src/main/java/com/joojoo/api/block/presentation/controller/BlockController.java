package com.joojoo.api.block.presentation.controller;

import com.joojoo.api.block.application.BlockService;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;
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

    @Operation(summary = "블럭(노트) 상세 페이지", description = "메인 페이지에서 더보기 버튼 누르면 자식, 자손 블럭까지 응답 합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BlockDetailResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "없는 블럭입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/detail/{blockId}")
    public BaseResponse<BlockDetailResponseDto> getBlockDetail(@PathVariable Long blockId) {
        return BaseResponse.ok(blockService.getBlockDetail(blockId));
    }

    @Operation(summary = "블럭(노트) 메인 페이지 API", description = "메인 페이지에 보여주는 부모 블럭 리스트 입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BlockMainViewResponse.class))
            )
    })
    @GetMapping("/detail")
    public BaseResponse<BlockMainViewResponse> getBlockMainView(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastDate
    ) {
        return BaseResponse.ok(blockService.getBlockMainView(user.getUserId(), lastDate));
    }

}
