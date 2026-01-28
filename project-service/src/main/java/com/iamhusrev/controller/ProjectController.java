package com.iamhusrev.controller;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.entity.ResponseWrapper;
import com.iamhusrev.exception.ProjectServiceException;
import com.iamhusrev.service.ProjectService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectFallbackHandler fallbackHandler;

    @GetMapping
    @CircuitBreaker(name = "project-service", fallbackMethod = "getProjectsFallback")
    public ResponseEntity<ResponseWrapper> getProjects() {
        List<ProjectDTO> projectDTOList = projectService.listAllProjects();
        return ResponseEntity.ok(new ResponseWrapper("Projects are successfully retrieved", projectDTOList, HttpStatus.OK));
    }

    @GetMapping("/{code}")
    @CircuitBreaker(name = "project-service", fallbackMethod = "getByCodeFallback")
    public ResponseEntity<ResponseWrapper> getProjectByCode(@PathVariable String code) {
        ProjectDTO projectDTO = projectService.getByProjectCode(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully retrieved", projectDTO, HttpStatus.OK));
    }

    @PostMapping
    @CircuitBreaker(name = "project-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> createProject(@RequestBody ProjectDTO project) throws ProjectServiceException {
        projectService.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper("Project is successfully created", HttpStatus.CREATED));
    }

    @PutMapping
    @CircuitBreaker(name = "project-service", fallbackMethod = "createUpdateFallback")
    public ResponseEntity<ResponseWrapper> updateProject(@RequestBody ProjectDTO project) throws ProjectServiceException {
        projectService.update(project);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully updated", project, HttpStatus.OK));
    }

    @DeleteMapping("/{projectCode}")
    @CircuitBreaker(name = "project-service", fallbackMethod = "actionFallback")
    public ResponseEntity<ResponseWrapper> deleteProject(@PathVariable("projectCode") String code) throws ProjectServiceException {
        projectService.delete(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully deleted", HttpStatus.OK));
    }

    @GetMapping("/details/{userName}")
    @CircuitBreaker(name = "project-service", fallbackMethod = "getDetailsFallback")
    public ResponseEntity<ResponseWrapper> readAllProjectDetails(@PathVariable String userName) throws ProjectServiceException {
        List<ProjectDTO> projectDTOs = projectService.listAllProjectDetails(userName);
        return ResponseEntity.ok(new ResponseWrapper("Projects are retrieved with details", projectDTOs, HttpStatus.OK));
    }

    @PutMapping("/manager/complete/{projectCode}")
    @CircuitBreaker(name = "project-service", fallbackMethod = "actionFallback")
    public ResponseEntity<ResponseWrapper> managerCompleteProject(@PathVariable("projectCode") String code) throws ProjectServiceException {
        projectService.complete(code);
        return ResponseEntity.ok(new ResponseWrapper("Project is successfully completed", HttpStatus.OK));
    }

    // -------------------------------------------------------------------------
    // FALLBACK BRIDGE METHODS
    // These methods delegate the actual logic to the ProjectFallbackHandler.
    // -------------------------------------------------------------------------

    public ResponseEntity<ResponseWrapper> getProjectsFallback(Throwable t) {
        return fallbackHandler.handleListFallback(t);
    }

    public ResponseEntity<ResponseWrapper> getByCodeFallback(String code, Throwable t) {
        return fallbackHandler.handleSingleResourceFallback(code, t);
    }

    // Handles both Create and Update fallbacks since signature matches
    public ResponseEntity<ResponseWrapper> createUpdateFallback(ProjectDTO project, Throwable t) {
        return fallbackHandler.handleModificationFallback(project, t);
    }

    // Handles both Delete and Complete fallbacks
    public ResponseEntity<ResponseWrapper> actionFallback(String code, Throwable t) {
        return fallbackHandler.handleActionFallback(code, t);
    }

    public ResponseEntity<ResponseWrapper> getDetailsFallback(String userName, Throwable t) {
        // Reusing the list fallback logic, but keeping the method signature distinct for Resilience4j
        return fallbackHandler.handleListFallback(t);
    }
}