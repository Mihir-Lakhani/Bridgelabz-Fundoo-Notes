package com.bridgelabz.fundoonotes.exception;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> badCredentials() {
        return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
    }
}
