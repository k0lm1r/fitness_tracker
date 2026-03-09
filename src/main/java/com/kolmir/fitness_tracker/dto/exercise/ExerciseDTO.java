package com.kolmir.fitness_tracker.dto.exercise;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExerciseDTO {
    private Long id;

    @NotNull(message = "id категории не может быть пустым")
    @Positive(message = "id категории должен быть больше 0")
    private Long categoryId;

    @NotBlank(message = "название не может быть пустым")
    @Size(min = 1, max = 100, message = "длина названия должна быть от 1 до 100 символов")
    private String name;

    @NotNull(message = "длительность должна содержать значение")
    @Positive(message = "длительность тренировки должна быть больше 0")
    private Integer durationMinutes;

    private Set<Long> workoutIds;
}
