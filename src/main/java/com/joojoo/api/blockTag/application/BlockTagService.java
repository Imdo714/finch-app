package com.joojoo.api.blockTag.application;

import com.joojoo.api.blockTag.presentation.dto.response.detail.TotalCountResponse;

public interface BlockTagService {

    TotalCountResponse getBlockCount(Long userId, Long tagId);
}
