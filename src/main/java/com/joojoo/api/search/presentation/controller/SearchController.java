package com.joojoo.api.search.presentation.controller;

import com.joojoo.api.search.application.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    // 검색한 기록은 Redis에 저장하고
    // 태그 저장하는 부분에도 그냥 Redis에 모음 자음 분해서 넣어야 할듯

}
