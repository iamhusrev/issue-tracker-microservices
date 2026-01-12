package com.iamhusrev.service;

import com.iamhusrev.dto.TaskDTO;
import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "task-service", path = "/api/v1/task", fallback = TaskClientFallback.class)
public interface TaskClientService {

    @GetMapping
    ResponseWrapper getTasks();

    @GetMapping("/{taskId}")
    ResponseWrapper getTaskById(@PathVariable("taskId") Long taskId);

    @PostMapping
    ResponseWrapper createTask(@RequestBody TaskDTO taskDTO);

    @DeleteMapping("/{taskId}")
    ResponseWrapper deleteTask(@PathVariable("taskId") Long taskId);

    @PutMapping
    ResponseWrapper updateTask(@RequestBody TaskDTO taskDTO);

    @GetMapping("/employee/pending-tasks")
    ResponseWrapper employeePendingTasks();

    @PutMapping("/employee/update/")
    ResponseWrapper employeeUpdateTasks(@RequestBody TaskDTO task);

    @GetMapping("/employee/archive")
    ResponseWrapper employeeArchivedTasks();
}