package com.kolmir.fitness_tracker.dto.exercise;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "Ответ с данными упражнения")
public class ExerciseResponse {
    @Schema(description = "Идентификатор упражнения", example = "7")
    private Long id;

    @Schema(description = "Идентификатор категории", example = "3")
    private Long categoryId;

    @Schema(description = "Название упражнения", example = "Планка")
    private String name;

    @Schema(description = "Длительность упражнения в минутах", example = "20")
    private Integer durationMinutes;

    @Schema(description = "Идентификаторы тренировок, в которых используется упражнение")
    private Set<Long> workoutIds;
}
