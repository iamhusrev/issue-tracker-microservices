package com.iamhusrev.service.impl;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.dto.UserDTO;
import com.iamhusrev.dto.UserResponseDTO;
import com.iamhusrev.entity.Project;
import com.iamhusrev.entity.User;
import com.iamhusrev.enums.Status;
import com.iamhusrev.exception.ProjectServiceException;
import com.iamhusrev.repository.ProjectRepository;
import com.iamhusrev.service.ProjectService;
import com.iamhusrev.service.UserClientService;
import com.iamhusrev.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MapperUtil mapperUtil;
    private final UserClientService userClientService;


    @Override
    public ProjectDTO getByProjectCode(String code) {
        Project project = projectRepository.findByProjectCode(code);
        return mapperUtil.convert(project, new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {

        List<Project> list = projectRepository.findAll();
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());

    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws ProjectServiceException {

        Project foundProject = projectRepository.findByProjectCode(dto.getProjectCode());

        if (foundProject != null) {
            throw new ProjectServiceException("Project with this code already existing");
        }

        Project obj = mapperUtil.convert(dto, new Project());

        Project createdProject = projectRepository.save(obj);

        return mapperUtil.convert(createdProject, new ProjectDTO());

    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws ProjectServiceException {

        Project project = projectRepository.findByProjectCode(dto.getProjectCode());

        if (project == null) {
            throw new ProjectServiceException("Project does not exist");
        }

        Project convertedProject = mapperUtil.convert(dto, new Project());

        Project updatedProject = projectRepository.save(convertedProject);

        return mapperUtil.convert(updatedProject, new ProjectDTO());

    }


    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);

        project.setIsDeleted(true);
        project.setProjectCode(project.getProjectCode() + "-" + project.getId());

        projectRepository.save(project);

//        taskService.deleteByProject(projectMapper.convertToDto(project));

    }

    @Override
    public ProjectDTO complete(String projectCode) {

        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);

        Project completedProject = projectRepository.save(project);

        return mapperUtil.convert(completedProject, new ProjectDTO());

//        taskService.completeByProject(projectMapper.convertToDto(project));
    }

    @Override
    public List<ProjectDTO> listAllProjectDetails(String userName) throws ProjectServiceException {

        UserResponseDTO userResponseDto = (UserResponseDTO) userClientService.getUserByUserName(userName).getData();

        UserDTO user = userResponseDto.getData();

        if (user != null) {
            List<Project> list = projectRepository.findAllByAssignedManagerId(user.getId());

            if (list.isEmpty()) {
                throw new ProjectServiceException("This manager does not have any project assigned");
            }

            return list.stream().map(project -> {
                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setProjectDetail(project.getProjectDetail());
                projectDTO.setProjectStatus(project.getProjectStatus());
                projectDTO.setProjectName(project.getProjectName());
                projectDTO.setProjectCode(project.getProjectCode());
                projectDTO.setAssignedManager(user);
                return projectDTO;
            }).collect(Collectors.toList());
        }
        throw new ProjectServiceException("user couldn't find");
    }


    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list = projectRepository.findAllByAssignedManager(user);
        return list.stream().map(obj -> mapperUtil.convert(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream()
                .map(project -> mapperUtil.convert(project, new ProjectDTO()))
                .collect(Collectors.toList());
    }


}
