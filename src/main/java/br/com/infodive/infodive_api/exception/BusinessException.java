package br.com.infodive.infodive_api.exception;

/**
 * Lançada quando uma regra de negócio é violada. Tratada como HTTP 400 pelo GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
