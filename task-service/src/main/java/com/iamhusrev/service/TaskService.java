package com.iamhusrev.service;

import com.iamhusrev.dto.ProjectDTO;
import com.iamhusrev.dto.TaskDTO;
import com.iamhusrev.dto.UserResponseDTO;
import com.iamhusrev.entity.Project;
import com.iamhusrev.entity.Task;
import com.iamhusrev.entity.User;
import com.iamhusrev.enums.Status;
import com.iamhusrev.respository.TaskRepository;
import com.iamhusrev.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final MapperUtil mapperUtil;
    private final UserClientService userClientService;


    public TaskDTO findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(value -> mapperUtil.convert(value, new TaskDTO())).orElse(null);
    }

    public List<TaskDTO> listAllTasks() {
        List<Task> list = taskRepository.findAll();
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    public void save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        Task task = mapperUtil.convert(dto, new Task());
        taskRepository.save(task);
    }

    public void update(TaskDTO dto) {

        Optional<Task> task = taskRepository.findById(dto.getId());
        Task convertedTask = mapperUtil.convert(dto, new Task());

        if (task.isPresent()) {
            convertedTask.setId(task.get().getId());
            convertedTask.setTaskStatus(dto.getTaskStatus() == null ? task.get().getTaskStatus() : dto.getTaskStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }

    }

    public void delete(Long id) {

        Optional<Task> foundTask = taskRepository.findById(id);

        if (foundTask.isPresent()) {
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }


    }

    public int totalNonCompletedTask(String projectCode) {
        return taskRepository.totalNonCompletedTasks(projectCode);
    }

    public int totalCompletedTask(String projectCode) {
        return taskRepository.totalCompletedTasks(projectCode);
    }

    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> list = listAllByProject(project);
        list.forEach(taskDTO -> delete(taskDTO.getId()));
    }

    public void completeByProject(ProjectDTO project) {
        List<TaskDTO> list = listAllByProject(project);
        list.forEach(taskDTO -> {
            taskDTO.setTaskStatus(Status.COMPLETE);
            update(taskDTO);
        });
    }

    public List<TaskDTO> listAllTasksByStatusIsNot(Status status) {

        UserResponseDTO userResponseDto = (UserResponseDTO) userClientService.getUserByUserName("john@employee.com").getData();

        User loggedInUser = mapperUtil.convert(userResponseDto.getData(), new User());

        List<Task> list = taskRepository.findAllByTaskStatusIsNotAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    public void updateStatus(TaskDTO dto) {

        Optional<Task> task = taskRepository.findById(dto.getId());

        if (task.isPresent()) {
            task.get().setTaskStatus(dto.getTaskStatus());
            taskRepository.save(task.get());
        }

    }

    public List<TaskDTO> listAllTasksByStatus(Status status) {
        UserResponseDTO userResponseDto = (UserResponseDTO) userClientService.getUserByUserName("john@employee.com").getData();

        User loggedInUser = mapperUtil.convert(userResponseDto.getData(), new User());

        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status, loggedInUser);
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    public List<TaskDTO> readAllByAssignedEmployee(User assignedEmployee) {
        List<Task> list = taskRepository.findAllByAssignedEmployee(assignedEmployee);
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

    private List<TaskDTO> listAllByProject(ProjectDTO project) {

        List<Task> list = taskRepository.findAllByProject(mapperUtil.convert(project, new Project()));
        return list.stream().map(obj -> mapperUtil.convert(obj, new TaskDTO())).collect(Collectors.toList());
    }

}
