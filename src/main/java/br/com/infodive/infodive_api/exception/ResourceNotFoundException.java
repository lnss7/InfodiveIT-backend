package br.com.infodive.infodive_api.exception;

/**
 * Lançada quando uma entidade não é encontrada. Tratada como HTTP 404 pelo GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
