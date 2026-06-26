package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HomeTrustStatsRequest(
        String eyebrow,
        String prefixo,
        int valor,
        int valorInicial,
        String sufixo,
        @NotBlank String titulo,
        String descricao,
        int ordem
) {}
