package com.example.task_manager.service;

import com.example.task_manager.metrics.TaskMetricsService;
import com.example.task_manager.model.Task;
import com.example.task_manager.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher taskEventPublisher;
    private final TaskMetricsService metricsService;

    public TaskService(TaskRepository taskRepository,
                       TaskEventPublisher taskEventPublisher,
                       TaskMetricsService metricsService) {
        this.taskRepository = taskRepository;
        this.taskEventPublisher = taskEventPublisher;
        this.metricsService = metricsService;
    }

    @Cacheable("tasks")
    public List<Task> getAllTasks() {
        System.out.println(">>> Fetching tasks from database...");
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTask(Task task) {
        return metricsService.getTaskCreationTimer().record(() -> {
            Task savedTask = taskRepository.save(task);
            taskEventPublisher.sendTaskCreatedEvent(savedTask);
            metricsService.incrementCreatedTasks();
            metricsService.incrementActiveTasks();
            return savedTask;
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(existing -> {
            existing.setTitle(updatedTask.getTitle());
            existing.setDescription(updatedTask.getDescription());
            existing.setStatus(updatedTask.getStatus());
            return taskRepository.save(existing);
        });
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
        metricsService.decrementActiveTasks();
    }
}
