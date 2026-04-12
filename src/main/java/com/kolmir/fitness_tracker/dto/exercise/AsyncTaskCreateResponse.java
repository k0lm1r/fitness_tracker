package com.kolmir.fitness_tracker.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Ответ о запуске асинхронной задачи")
public class AsyncTaskCreateResponse {
    @Schema(description = "Идентификатор задачи", example = "0b4b2be3-4bc2-4b34-bdb6-f1230f47f50d")
    private String taskId;
}
