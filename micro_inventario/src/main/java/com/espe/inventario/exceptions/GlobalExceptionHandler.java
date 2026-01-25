package com.espe.inventario.exceptions;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        log.info("Recurso no encontrado: {}", ex.getMessage());
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "NOT_FOUND", ex.getMessage(), null);
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeign(FeignException ex) {
        log.error("Error técnico en cliente Feign: {}", ex.getMessage(), ex);
        ApiError err = new ApiError(HttpStatus.BAD_GATEWAY.value(), "EXTERNAL_SERVICE_ERROR",
                "No fue posible comunicarse con un servicio externo. Intente nuevamente más tarde.", null);
        return new ResponseEntity<>(err, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.info("Validación fallida: {}", details);
        ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR",
                "Entrada inválida", details);
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Error interno: {}", ex.getMessage(), ex);
        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR",
                "Ocurrió un error interno. Intente nuevamente más tarde.", null);
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
