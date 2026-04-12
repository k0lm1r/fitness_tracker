package com.kolmir.fitness_tracker.models;

import java.time.Instant;

import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskStatusResponse;

public class AsyncTaskState {
    private final String id;
    private volatile TaskStatus status;
    private final Instant createdAt;
    private volatile Instant startedAt;
    private volatile Instant finishedAt;
    private volatile Integer processedCount;
    private volatile String errorMessage;

    public AsyncTaskState(String id) {
        this.id = id;
        this.status = TaskStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public synchronized void markRunning() {
        this.status = TaskStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public synchronized void markCompleted(int processedCount) {
        this.status = TaskStatus.COMPLETED;
        this.processedCount = processedCount;
        this.finishedAt = Instant.now();
    }

    public synchronized void markFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.finishedAt = Instant.now();
    }

    public AsyncTaskStatusResponse toResponse(int completedTasksTotal) {
        return new AsyncTaskStatusResponse(
                id,
                status.name(),
                createdAt,
                startedAt,
                finishedAt,
                processedCount,
                errorMessage,
                completedTasksTotal);
    }
}
