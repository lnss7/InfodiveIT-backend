package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.EtapaItem;
import java.util.List;

public record ServicosEtapasRequest(
        String eyebrow,
        String headline,
        String subtitulo,
        List<EtapaItem> etapas
) {}
