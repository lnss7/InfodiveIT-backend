package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FabricanteRequest(
        @NotBlank String slug,
        @NotBlank String nome,
        String descricao,
        String descricaoCurta,
        String logoUrl,
        String siteOficial,
        boolean destaque,
        int ordem
) {}
