package com.iamhusrev.controller;

import com.iamhusrev.dto.TaskDTO;
import com.iamhusrev.entity.ResponseWrapper;
import com.iamhusrev.enums.Status;
import com.iamhusrev.service.TaskService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskFallbackHandler fallbackHandler;

    @GetMapping
    @CircuitBreaker(name = "task-service", fallbackMethod = "getTasksFallback")
    public ResponseEntity<ResponseWrapper> getTasks() {
        List<TaskDTO> taskDTOList = taskService.listAllTasks();
        return ResponseEntity.ok(new ResponseWrapper("Task are successfully retrieved", taskDTOList, HttpStatus.OK));
    }

    @GetMapping("/{taskId}")
    @CircuitBreaker(name = "task-service", fallbackMethod = "getTaskByIdFallback")
    public ResponseEntity<ResponseWrapper> getTaskById(@PathVariable Long taskId) {
        TaskDTO taskDTO = taskService.findById(taskId);
        return ResponseEntity.ok(new ResponseWrapper("Task is successfully retrieved", taskDTO, HttpStatus.OK));
    }

    @PostMapping
    @CircuitBreaker(name = "task-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        taskService.save(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("Task is successfully created", HttpStatus.CREATED));
    }

    @DeleteMapping("/{taskId}")
    @CircuitBreaker(name = "task-service", fallbackMethod = "deleteTaskFallback")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.ok(new ResponseWrapper("Task is successfully deleted", HttpStatus.OK));
    }

    @PutMapping
    @CircuitBreaker(name = "task-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> updateTask(@Valid @RequestBody TaskDTO taskDTO) {
        taskService.update(taskDTO);
        return ResponseEntity.ok(new ResponseWrapper("Task is successfully updated", HttpStatus.OK));
    }

    @GetMapping("/employee/pending-tasks/{userName}")
    @CircuitBreaker(name = "task-service", fallbackMethod = "employeePendingTasksFallback")
    public ResponseEntity<ResponseWrapper> employeePendingTasks(@PathVariable String userName) {
        List<TaskDTO> taskDTOList = taskService.listAllTasksByStatusIsNot(Status.COMPLETE, userName);
        return ResponseEntity.ok(new ResponseWrapper("Tasks are successfully retrieved", taskDTOList, HttpStatus.OK));
    }

    @PutMapping("/employee/update/")
    @CircuitBreaker(name = "task-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> employeeUpdateTasks(@RequestBody TaskDTO task) {
        taskService.updateStatus(task);
        return ResponseEntity.ok(new ResponseWrapper("Task is successfully updated", HttpStatus.OK));
    }

    @GetMapping("/employee/archive/{userName}")
    @CircuitBreaker(name = "task-service", fallbackMethod = "employeeArchivedTasksFallback")
    public ResponseEntity<ResponseWrapper> employeeArchivedTasks(@PathVariable String userName) {
        List<TaskDTO> taskDTOS = taskService.listAllTasksByStatus(Status.COMPLETE, userName);
        return ResponseEntity.ok(new ResponseWrapper("Tasks are successfully retrieved", taskDTOS, HttpStatus.OK));
    }

    // -------------------------------------------------------------------------
    // FALLBACK BRIDGE METHODS
    // -------------------------------------------------------------------------

    public ResponseEntity<ResponseWrapper> getTasksFallback(Throwable t) {
        return fallbackHandler.handleListFallback(t);
    }

    public ResponseEntity<ResponseWrapper> getTaskByIdFallback(Long taskId, Throwable t) {
        return fallbackHandler.handleSingleResourceFallback(taskId, t);
    }

    public ResponseEntity<ResponseWrapper> createUpdateFallback(TaskDTO task, Throwable t) {
        return fallbackHandler.handleModificationFallback(task, t);
    }

    public ResponseEntity<ResponseWrapper> deleteTaskFallback(Long taskId, Throwable t) {
        return fallbackHandler.handleActionFallback(taskId, t);
    }

    public ResponseEntity<ResponseWrapper> employeePendingTasksFallback(String userName, Throwable t) {
        return fallbackHandler.handleListFallback(t);
    }

    public ResponseEntity<ResponseWrapper> employeeArchivedTasksFallback(String userName, Throwable t) {
        return fallbackHandler.handleListFallback(t);
    }
}