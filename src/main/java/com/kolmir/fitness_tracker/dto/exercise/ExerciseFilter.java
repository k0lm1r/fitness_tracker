package com.kolmir.fitness_tracker.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "Фильтр для поиска упражнений")
public class ExerciseFilter {
    @Schema(description = "Фильтр по названию упражнения", example = "Бег")
    private String name;

    @Schema(description = "Фильтр по идентификатору категории", example = "3")
    private Long categoryId;

    @Schema(description = "Фильтр по названию категории", example = "Кардио")
    private String categoryName;

    @Schema(description = "Идентификатор владельца", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long ownerId;

    @Schema(description = "Минимальная длительность в минутах", example = "15")
    private Integer durationMinutesFrom;

    @Schema(description = "Максимальная длительность в минутах", example = "90")
    private Integer durationMinutesTo;
}
