package com.bridgelabz.fundoonotes.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rabbit")
@EnableRabbit
public class RabbitMqConfig {
    public static final String REMINDER_QUEUE = "fundoo.reminders";

    @Bean
    public Queue reminderQueue() {
        return new Queue(REMINDER_QUEUE, true);
    }
}
