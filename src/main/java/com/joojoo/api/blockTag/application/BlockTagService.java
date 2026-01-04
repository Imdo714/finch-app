package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.presentation.dto.response.detail.BlockTagsResponse;
import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;

import java.time.LocalDate;

public interface BlockTagService {

    BlockTagsResponse getUserTagIdsByTagId(Long userId, Long tagId, LocalDate lastDate);

    TotalCountResponse getBlockCount(Long userId, Long tagId);
}
