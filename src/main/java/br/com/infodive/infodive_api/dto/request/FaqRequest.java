package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FaqRequest(
        @NotBlank String pergunta,
        @NotBlank String resposta,
        int ordem
) {}
