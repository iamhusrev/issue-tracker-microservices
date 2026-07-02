package com.iamhusrev.controller;

import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
public class FallbackController {

    @RequestMapping("/auth")
    public ResponseEntity<ResponseWrapper> authFallback() {
        return fallback("Auth service is warming up or temporarily unavailable.");
    }

    @RequestMapping("/user")
    public ResponseEntity<ResponseWrapper> userFallback() {
        return fallback("User service is warming up or temporarily unavailable.");
    }

    @RequestMapping("/project")
    public ResponseEntity<ResponseWrapper> projectFallback() {
        return fallback("Project service is warming up or temporarily unavailable.");
    }

    @RequestMapping("/task")
    public ResponseEntity<ResponseWrapper> taskFallback() {
        return fallback("Task service is warming up or temporarily unavailable.");
    }

    private ResponseEntity<ResponseWrapper> fallback(String message) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper(message, HttpStatus.SERVICE_UNAVAILABLE));
    }
}
