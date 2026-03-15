package com.kolmir.fitness_tracker.dto.workout;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Schema(description = "Ответ с данными тренировки")
public class WorkoutResponse {
    @Schema(description = "Идентификатор тренировки", example = "10")
    private Long id;

    @Schema(description = "Название тренировки", example = "Утренняя тренировка")
    private String name;

    @Schema(description = "Идентификатор владельца тренировки", example = "1")
    private Long ownerId;

    @Schema(description = "Идентификаторы упражнений, входящих в тренировку")
    private Set<Long> exerciseIds;
}
