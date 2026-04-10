package com.iamhusrev.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserEvent.class, name = "UserEvent"),
        @JsonSubTypes.Type(value = ProjectEvent.class, name = "ProjectEvent"),
        @JsonSubTypes.Type(value = TaskEvent.class, name = "TaskEvent")
})
public abstract class BaseEvent implements Serializable {

    private String eventId;
    private String eventType;
    private String serviceName;
    private LocalDateTime timestamp;

    protected BaseEvent(String eventType, String serviceName) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.serviceName = serviceName;
        this.timestamp = LocalDateTime.now();
    }
}
