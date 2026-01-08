package com.joojoo.api.block.presentation.controller;

import com.joojoo.api.block.application.port.in.CreateBlockUseCase;
import com.joojoo.api.block.application.port.in.DeleteBlockUseCase;
import com.joojoo.api.block.application.port.in.GetBlockUseCase;
import com.joojoo.api.block.application.port.in.UpdateBlockUseCase;
import com.joojoo.api.block.domain.model.enums.DeleteMode;
import com.joojoo.api.block.presentation.dto.request.createBlock.BlockSaveRequestDto;
import com.joojoo.api.block.presentation.dto.request.updateBlock.BlockUpdateDto;
import com.joojoo.api.block.presentation.dto.response.blockDetail.BlockResponse;
import com.joojoo.api.block.presentation.dto.response.detail.BlockDetailResponseDto;
import com.joojoo.api.block.presentation.dto.response.mainView.BlockMainViewResponse;
import com.joojoo.api.common.domain.request.auth.CustomUserDetails;
import com.joojoo.api.common.domain.response.BaseResponse;
import com.joojoo.api.common.domain.response.ErrorResponse;
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
    
    private final GetBlockUseCase getBlockUseCase;
    private final CreateBlockUseCase createBlockUseCase;
    private final UpdateBlockUseCase updateBlockUseCase;
    private final DeleteBlockUseCase deleteBlockUseCase;

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
        return BaseResponse.ok(createBlockUseCase.saveBlockTree(user.getUserId(), requestDto));
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
        return BaseResponse.ok(getBlockUseCase.getBlockDetail(blockId));
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
        return BaseResponse.ok(getBlockUseCase.getBlockMainView(user.getUserId(), lastDate));
    }

    @Operation(summary = "블럭(노트) 삭제 API", description = "블럭 삭제 API입니다. 부모만 삭제하면 자식들이 한단계식 승급을하는 형식입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "승격될 자식 블록이 한도(3개)를 초과합니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "해당 블록에 대한 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 블록을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/delete/{blockId}")
    public BaseResponse<String> deleteBlock(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long blockId,
            @RequestParam(defaultValue = "SINGLE") DeleteMode mode
    ) {
        deleteBlockUseCase.deleteBlock(user.getUserId(), blockId, mode);
        return BaseResponse.ok("삭제 성공!");
    }

    @Operation(summary = "블럭(노트) 수정 API", description = "블럭(노트) 내용을 수정하는 API입니다. ")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "해당 블록에 대한 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 블록을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/update/{blockId}")
    public BaseResponse<String> updateBlock(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long blockId,
            @RequestBody BlockUpdateDto blockUpdateDto
    ) {
        updateBlockUseCase.updateBlock(user.getUserId(), blockId, blockUpdateDto);
        return BaseResponse.ok("수정 성공!");
    }

}
