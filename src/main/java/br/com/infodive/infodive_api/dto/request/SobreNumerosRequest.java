package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.StatItem;
import java.util.List;

public record SobreNumerosRequest(
        String textoDescritivo,
        List<StatItem> stats
) {}
