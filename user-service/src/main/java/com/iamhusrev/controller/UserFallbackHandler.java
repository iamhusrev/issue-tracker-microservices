package com.iamhusrev.controller;

import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.entity.ResponseWrapper;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class UserFallbackHandler {

    // 1. Tracer nesnesini tanımlıyoruz (Spring Boot 3 / Micrometer Tracing)
    private final Tracer tracer;

    // 2. Constructor Injection ile içeri alıyoruz
    public UserFallbackHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    public ResponseEntity<ResponseWrapper> handleListFallback(Throwable t) {
        // Loglama ve Tracing işlemini ortak metoda gönderiyoruz
        tagZipkinAsError("Failed to retrieve user list", t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. Unable to retrieve users.", Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleSingleUserFallback(String username, Throwable t) {
        tagZipkinAsError("Failed to retrieve user: " + username, t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. User details not found.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleUserModificationFallback(UserDTO user, Throwable t) {
        tagZipkinAsError("Failed to create/update user", t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service busy. User operation failed.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleUserDeletionFallback(String username, Throwable t) {
        tagZipkinAsError("Failed to delete user: " + username, t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. Could not delete user.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    /**
     * Bu metod hem console logu basar hem de Zipkin Span'ine hata etiketi yapıştırır.
     */
    private void tagZipkinAsError(String message, Throwable t) {
        // 1. Standart Loglama (Console/File)
        log.warn("Circuit Breaker OPEN: {}", message);
        log.error("Circuit Breaker Fallback Triggered. Context: {}", message, t);

        // 2. Zipkin (Distributed Tracing) İşaretleme
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            // 'error' tag'i Zipkin'de satırı KIRMIZI yapar
            currentSpan.tag("error", message);

            // Hatanın teknik detayını da ekleyelim ki Zipkin arayüzünde görebil
            if (t != null) {
                currentSpan.tag("exception.type", t.getClass().getSimpleName());
                currentSpan.tag("exception.message", t.getMessage() != null ? t.getMessage() : "No message");
            }
        }
    }
}