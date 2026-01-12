package com.iamhusrev.service;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.entity.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "project-service", path = "/api/v1/project", fallback = UserClientFallback.class)
public interface ProjectClientService {

    @GetMapping
    ResponseWrapper getProjects();

    @GetMapping("/{code}")
    ResponseWrapper getProjectByCode(@PathVariable("code") String code);

    @PostMapping
    ResponseWrapper createProject(@RequestBody ProjectDTO project);

    @PutMapping
    ResponseWrapper updateProject(@RequestBody ProjectDTO project);

    @DeleteMapping("/{projectCode}")
    ResponseWrapper deleteProject(@PathVariable("projectCode") String projectCode);

    @GetMapping("/details/{userName}")
    ResponseWrapper readAllProjectDetails(@PathVariable("userName") String userName);

    @PutMapping("/manager/complete/{projectCode}")
    ResponseWrapper managerCompleteProject(@PathVariable("projectCode") String projectCode);
}