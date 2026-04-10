package com.iamhusrev.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectEvent extends BaseEvent {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private String assignedManagerUserName;

    public ProjectEvent(String eventType, Long projectId, String projectCode,
                        String projectName, String assignedManagerUserName) {
        super(eventType, "project-service");
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.assignedManagerUserName = assignedManagerUserName;
    }
}
