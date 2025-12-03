package com.joojoo.global.common.response;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record SliceResponse<T>(
    List<T> content,  // 게시글 데이터를 담는 리스트
    boolean hasNext,            // 다음 페이지 존재 여부
    int currentPage,            // 현재 페이지 번호
    int pageSize
) {
    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return SliceResponse.<T>builder()
            .content(slice.getContent())
            .hasNext(slice.hasNext())
            .currentPage(slice.getNumber() + 1) // Spring Data JPA는 0부터 시작하므로 +1
            .pageSize(slice.getSize())
            .build();
    }
}