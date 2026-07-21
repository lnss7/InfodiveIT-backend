package br.com.infodive.infodive_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record HomeSolucoesBentoRequest(
        @NotBlank String nome,
        String descricao,
        String icone,
        String imagemIaUrl,
        String textoCarrossel,
        int ordem,
        UUID solucaoId
) {}
