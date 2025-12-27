package com.joojoo.api.blockTag.presentation.dto.response.all;

import com.joojoo.api.blockTag.domain.model.entity.BlockTag;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class TagDateResult {
    private List<BlockTag> content;
    private List<LocalDate> targetDates;
}
