package com.iamhusrev.controller;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.entity.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class ProjectFallbackHandler {

    public ResponseEntity<ResponseWrapper> handleListFallback(Throwable t) {
        logWithStackTrace("Failed to retrieve project list", t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service is currently unavailable. Unable to retrieve projects.", Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleSingleResourceFallback(String identifier, Throwable t) {
        logWithStackTrace("Failed to retrieve details for ID: " + identifier, t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service is currently unavailable. Unable to retrieve project details.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleModificationFallback(ProjectDTO project, Throwable t) {
        logWithStackTrace("Failed to modify project", t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service is busy. Operation could not be completed.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleActionFallback(String code, Throwable t) {
        logWithStackTrace("Failed to perform action on project: " + code, t);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service is currently unavailable. Action failed.", HttpStatus.SERVICE_UNAVAILABLE));
    }


    private void logWithStackTrace(String message, Throwable t) {
        log.error("Circuit Breaker Fallback Triggered. Context: {}", message, t);
    }
}