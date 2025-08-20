package com.autobots.automanager.controles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

@RestControllerAdvice
public class RespostaStatusException {
    private static final Logger logger = LoggerFactory.getLogger(RespostaStatusException.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroControle> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {

        logger.warn("Status {}: {} at {}", ex.getStatusCode(), ex.getReason(), request.getRequestURI());
        ErroControle erro = new ErroControle(ex.getStatusCode().value(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroControle> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        String message = String.join("; ", erros);
        logger.warn("Bad Request: {} at {}", message, request.getRequestURI());
        ErroControle erro = new ErroControle(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroControle> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> erros = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        String message = String.join("; ", erros);
        logger.warn("Constraint Violation: {} at {}", message, request.getRequestURI());
        ErroControle erro = new ErroControle(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(erro);
    }
}
