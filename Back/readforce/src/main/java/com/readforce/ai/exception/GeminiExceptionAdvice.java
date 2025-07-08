package com.readforce.ai.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeminiExceptionAdvice {

    @ExceptionHandler(GeminiException.class)
    public ResponseEntity<String> handleGeminiException(GeminiException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
