package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record HomeSegurancaMarqueeRequest(
        String icone,
        @NotBlank String titulo,
        @NotBlank String corpo,
        int ordem
) {}
