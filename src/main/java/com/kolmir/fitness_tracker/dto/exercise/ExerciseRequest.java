package com.kolmir.fitness_tracker.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "Запрос на создание или обновление упражнения")
public class ExerciseRequest {
    @Schema(description = "Идентификатор категории", example = "3")
    @NotNull(message = "id категории не может быть пустым")
    @Positive(message = "id категории должен быть больше 0")
    private Long categoryId;

    @Schema(description = "Название упражнения", example = "Планка")
    @NotBlank(message = "название не может быть пустым")
    @Size(min = 1, max = 100, message = "длина названия должна быть от 1 до 100 символов")
    private String name;

    @Schema(description = "Длительность упражнения в минутах", example = "20")
    @NotNull(message = "длительность должна содержать значение")
    @Positive(message = "длительность тренировки должна быть больше 0")
    private Integer durationMinutes;
}
