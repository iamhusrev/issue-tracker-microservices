package com.iamhusrev.service;

import com.iamhusrev.dto.TaskDTO;
import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskClientFallback implements TaskClientService {

    @Override
    public ResponseWrapper getTasks() {
        return new ResponseWrapper("Task service is currently unavailable", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper getTaskById(Long taskId) {
        return new ResponseWrapper("Task service unavailable, cannot fetch task ID: " + taskId, null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper createTask(TaskDTO taskDTO) {
        return new ResponseWrapper("Cannot create task right now, service down", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper deleteTask(Long taskId) {
        return new ResponseWrapper("Cannot delete task right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper updateTask(TaskDTO taskDTO) {
        return new ResponseWrapper("Cannot update task right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper employeePendingTasks() {
        return new ResponseWrapper("Cannot fetch pending tasks, service unavailable", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper employeeUpdateTasks(TaskDTO task) {
        return new ResponseWrapper("Cannot update task status right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper employeeArchivedTasks() {
        return new ResponseWrapper("Cannot fetch archived tasks, service unavailable", null, HttpStatus.SERVICE_UNAVAILABLE);
    }
}