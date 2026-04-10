package com.iamhusrev.listener;

import com.iamhusrev.event.BaseEvent;
import com.iamhusrev.event.ProjectEvent;
import com.iamhusrev.event.RabbitMQConfig;
import com.iamhusrev.event.TaskEvent;
import com.iamhusrev.event.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEvent(BaseEvent event) {
        log.info("=== NOTIFICATION ===");
        log.info("Event ID: {}", event.getEventId());
        log.info("Event Type: {}", event.getEventType());
        log.info("Service: {}", event.getServiceName());
        log.info("Timestamp: {}", event.getTimestamp());

        if (event instanceof UserEvent ue) {
            log.info("User: {} ({} {})", ue.getUserName(), ue.getFirstName(), ue.getLastName());
        } else if (event instanceof ProjectEvent pe) {
            log.info("Project: {} - {}", pe.getProjectCode(), pe.getProjectName());
        } else if (event instanceof TaskEvent te) {
            log.info("Task: {} [{}] - Status: {}", te.getTaskSubject(), te.getProjectCode(), te.getStatus());
        }

        log.info("====================");
    }
}
