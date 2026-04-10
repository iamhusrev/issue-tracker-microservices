package com.iamhusrev.event;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "issue-tracker.events";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    @Bean
    public TopicExchange issueTrackerExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange issueTrackerExchange) {
        return BindingBuilder.bind(notificationQueue).to(issueTrackerExchange).with("#");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
