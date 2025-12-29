package com.joojoo.api.util.filter.application;

import com.joojoo.api.blockTag.presentation.dto.request.TagListDto;
import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;

import java.time.LocalDate;

public interface FilterService {
    BlockTagsResponse getFilterCategory(Long userId, TagListDto tagListDto, LocalDate lastDate);
}
