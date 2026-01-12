package com.iamhusrev.controller;

import com.iamhusrev.dto.TaskDTO;
import com.iamhusrev.entity.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class TaskFallbackHandler {

    public ResponseEntity<ResponseWrapper> handleListFallback(Throwable t) {
        logWithStackTrace("Failed to retrieve task list", t);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. Unable to retrieve tasks.", Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleSingleResourceFallback(Long id, Throwable t) {
        logWithStackTrace("Failed to retrieve task with ID: " + id, t);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. Unable to retrieve task details.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleModificationFallback(TaskDTO task, Throwable t) {
        logWithStackTrace("Failed to process task modification", t);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service busy. Task could not be saved/updated.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    public ResponseEntity<ResponseWrapper> handleActionFallback(Long id, Throwable t) {
        logWithStackTrace("Failed to perform action on task ID: " + id, t);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseWrapper("Service unavailable. Action failed.", HttpStatus.SERVICE_UNAVAILABLE));
    }

    private void logWithStackTrace(String message, Throwable t) {
        log.warn("Circuit Breaker OPEN: {}", message);
        log.error("Circuit Breaker Fallback Triggered. Context: {}", message, t);
    }
}