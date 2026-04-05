package com.kolmir.fitness_tracker.dto.workout;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Schema(description = "Запрос на создание или обновление тренировки")
public class WorkoutRequest {
    @Schema(description = "Название тренировки", example = "Утренняя тренировка")
    @NotBlank
    @Size(min = 1, max = 100, message = "workout name should be from 1 to 100 characters")
    private String name;

    @Schema(description = "Идентификаторы упражнений тренировки")
    private Set<Long> exerciseIds;
}
