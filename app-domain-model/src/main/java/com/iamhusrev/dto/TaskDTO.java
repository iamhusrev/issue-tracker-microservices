package com.iamhusrev.dto;

import com.iamhusrev.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class TaskDTO {

    private Long id;

    @NotNull(message = "Project is required")
    private ProjectDTO project;

    @NotNull(message = "Assigned employee is required")
    private UserDTO assignedEmployee;

    @NotBlank(message = "Task subject is required")
    @Size(min = 2, max = 200, message = "Task subject must be between 2 and 200 characters")
    private String taskSubject;

    private String taskDetail;
    private Status taskStatus;
    private LocalDate assignedDate;
}
