package com.example.task_manager.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskMetricsService {

    private final Counter taskCreatedCounter;
    private final AtomicInteger activeTaskCount;
    private final Gauge taskActiveGauge;
    private final Timer taskCreationTimer;

    public TaskMetricsService(MeterRegistry meterRegistry) {
        this.taskCreatedCounter = Counter.builder("task_total")
                .description("Total number of tasks created")
                .tag("source", "api")
                .register(meterRegistry);

        this.activeTaskCount = new AtomicInteger(0);
        this.taskActiveGauge = Gauge.builder("task_active_count", activeTaskCount, AtomicInteger::get)
                .description("Current number of active tasks")
                .register(meterRegistry);

        this.taskCreationTimer = Timer.builder("task_creation_duration")
                .description("Time taken to create a task")
                .tag("source", "api")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }

    public void incrementCreatedTasks() {
        taskCreatedCounter.increment();
    }

    public void incrementActiveTasks() {
        activeTaskCount.incrementAndGet();
    }

    public void decrementActiveTasks() {
        activeTaskCount.decrementAndGet();
    }

    public Timer getTaskCreationTimer() {
        return taskCreationTimer;
    }
}
