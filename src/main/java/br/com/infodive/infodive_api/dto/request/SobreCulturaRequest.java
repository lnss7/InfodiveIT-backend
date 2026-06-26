package br.com.infodive.infodive_api.dto.request;

import br.com.infodive.infodive_api.entity.FotoItem;
import java.util.List;

public record SobreCulturaRequest(
        String eyebrow,
        String headline,
        String paragrafo,
        List<FotoItem> fotos
) {}
