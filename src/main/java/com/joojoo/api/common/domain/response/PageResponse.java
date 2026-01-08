package com.joojoo.api.common.domain.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageResponse<T>(
    List<T> content,    // 데이터 목록
    int totalPages,     // 총 페이지 수
    long totalElements, // 총 데이터 개수
    boolean hasNext,    // 다음 페이지 존재 여부
    int currentPage,    // 현재 페이지 번호
    int pageSize        // 페이지 크기
) {
    // Page 객체를 이용해 PageResponse 생성
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .hasNext(page.hasNext())
            .currentPage(page.getNumber() + 1)
            .pageSize(page.getSize())
            .build();
    }
}