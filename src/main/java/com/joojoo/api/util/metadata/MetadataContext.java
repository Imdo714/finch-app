package com.joojoo.api.util.metadata;

import com.joojoo.api.tag.application.in.TagInService;
import com.joojoo.api.tag.domain.model.entity.Tag;
import com.joojoo.api.ticker.application.in.TickerInService;
import com.joojoo.api.ticker.domain.model.entity.Ticker;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class MetadataContext {
    private final Set<String> tickerNames = new HashSet<>();
    private final Set<String> tagNames = new HashSet<>();

    private Map<String, Ticker> tickerMap = new HashMap<>();
    private Map<String, Tag> tagMap = new HashMap<>();

    public void addTicker(String name) {
        if (name != null) tickerNames.add(name);
    }

    public void addTag(String name) {
        if (name != null) tagNames.add(name);
    }

    /** DB에서 실제 엔티티들을 일괄 조회하여 Map에 저장 */
    public void loadEntities(TickerInService tickerService, TagInService tagService) {
        if (!tickerNames.isEmpty()) {
            this.tickerMap = tickerService.getTickerMap(tickerNames);
        }
        if (!tagNames.isEmpty()) {
            this.tagMap = tagService.getOrCreateTagMap(tagNames);
        }
    }

    public Ticker getTicker(String name) {
        return tickerMap.get(name);
    }

    public Tag getTag(String name) {
        return tagMap.get(name);
    }

}
