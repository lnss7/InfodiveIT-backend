package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.ValorItem;
import java.util.List;

public record SobreValoresRequest(
        String eyebrow,
        String headline,
        String paragrafo,
        List<ValorItem> valores
) {}
