package com.iamhusrev.event;

import com.iamhusrev.enums.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskEvent extends BaseEvent {

    private Long taskId;
    private String taskSubject;
    private String projectCode;
    private String assignedEmployeeUserName;
    private Status status;

    public TaskEvent(String eventType, Long taskId, String taskSubject,
                     String projectCode, String assignedEmployeeUserName, Status status) {
        super(eventType, "task-service");
        this.taskId = taskId;
        this.taskSubject = taskSubject;
        this.projectCode = projectCode;
        this.assignedEmployeeUserName = assignedEmployeeUserName;
        this.status = status;
    }
}
