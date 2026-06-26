package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HomeProblemasRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        String solucaoIndicada,
        String href,
        int ordem
) {}
