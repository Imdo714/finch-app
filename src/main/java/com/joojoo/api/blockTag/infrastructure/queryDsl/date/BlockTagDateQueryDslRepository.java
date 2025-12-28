package com.joojoo.api.blockTag.infrastructure.queryDsl.date;

import com.joojoo.api.blockTag.presentation.dto.response.all.TagDateResult;

import java.time.LocalDate;

public interface BlockTagDateQueryDslRepository {
    TagDateResult findAllByTagAndDate(Long userId, Long tagId, LocalDate targetDate);
}
