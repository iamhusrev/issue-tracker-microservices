package com.iamhusrev.service;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectClientFallback implements ProjectClientService {

    @Override
    public ResponseWrapper getProjects() {
        return new ResponseWrapper("Project service is currently unavailable", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper getProjectByCode(String code) {
        return new ResponseWrapper("Project service unavailable, cannot fetch project code: " + code, null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper createProject(ProjectDTO project) {
        return new ResponseWrapper("Cannot create project right now, service down", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper updateProject(ProjectDTO project) {
        return new ResponseWrapper("Cannot update project right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper deleteProject(String projectCode) {
        return new ResponseWrapper("Cannot delete project right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper readAllProjectDetails(String userName) {
        return new ResponseWrapper("Cannot fetch project details for user: " + userName, null, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseWrapper managerCompleteProject(String projectCode) {
        return new ResponseWrapper("Cannot complete project action right now", null, HttpStatus.SERVICE_UNAVAILABLE);
    }
}