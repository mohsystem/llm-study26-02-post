package com.um.springbootprojstructure.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), "invalid");
        }
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "fields", errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArg(IllegalArgumentException ex) {
        String code = ex.getMessage() == null ? "bad_request" : ex.getMessage();

        if ("invalid_credentials".equals(code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid_credentials"));
        }
        if ("not_found".equals(code)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not_found"));
        }
        if ("invalid_password_rules".equals(code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "invalid_password_rules"));
        }
        if ("password_policy_violation".equals(code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "password_policy_violation"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "bad_request"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> illegalState(IllegalStateException ex) {
        // includes crypto_error/misconfig without details
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", "service_unavailable"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> fallback(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "server_error"));
    }
}
