package br.com.infodive.infodive_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Espelha o CategoriaDTO esperado pelo frontend (src/lib/api.ts).
 * No banco a tabela chama-se {@code solucoes}; o frontend consome como "categoria".
 * Mapeamentos: {@code nome} ← titulo, {@code descricaoCompleta} ← overview.
 */
public record SolucaoResponse(
        UUID id,
        String nome,
        String slug,
        String icone,
        String descricaoCurta,
        String descricaoCompleta,
        int ordem,
        boolean ativo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
