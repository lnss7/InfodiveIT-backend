package br.com.infodive.infodive_api.exception;

import java.time.LocalDateTime;

/**
 * Formato padrão de resposta de erro retornado por toda a API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}
