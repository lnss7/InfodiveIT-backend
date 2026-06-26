package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.MetricaItem;
import br.com.infodive.infodive_api.entity.PilarItem;
import java.util.List;

public record ServicosMetodologiaRequest(
        String eyebrow,
        String headline,
        String paragrafo,
        List<MetricaItem> metricas,
        List<PilarItem> pilares
) {}
