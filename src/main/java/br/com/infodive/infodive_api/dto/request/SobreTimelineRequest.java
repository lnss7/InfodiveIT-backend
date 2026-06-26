package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.MarcoItem;
import java.util.List;

public record SobreTimelineRequest(
        String eyebrow,
        String headline,
        List<MarcoItem> marcos
) {}
