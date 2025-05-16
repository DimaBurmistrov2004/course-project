package com.example.task_manager.service;

import com.example.task_manager.config.RabbitMQConfig;
import com.example.task_manager.model.Task;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleTaskCreated(Task task) {
        System.out.println("Received Task Event: " + task.getTitle());
        // Здесь можно добавить запись в лог, аудит, уведомление и т.д.
    }
}
