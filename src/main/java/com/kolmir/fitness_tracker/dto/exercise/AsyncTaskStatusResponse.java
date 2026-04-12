package com.kolmir.fitness_tracker.dto.exercise;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Статус асинхронной задачи")
public class AsyncTaskStatusResponse {
    @Schema(description = "Идентификатор задачи", example = "0b4b2be3-4bc2-4b34-bdb6-f1230f47f50d")
    private String id;

    @Schema(description = "Текущий статус", example = "RUNNING")
    private String status;

    @Schema(description = "Время создания задачи")
    private Instant createdAt;

    @Schema(description = "Время старта выполнения задачи")
    private Instant startedAt;

    @Schema(description = "Время завершения задачи")
    private Instant finishedAt;

    @Schema(description = "Количество обработанных записей", example = "120")
    private Integer processedCount;

    @Schema(description = "Сообщение об ошибке (только при статусе FAILED)")
    private String errorMessage;

    @Schema(description = "Потокобезопасный счётчик успешно завершённых async-задач", example = "3")
    private Integer completedTasksTotal;
}
