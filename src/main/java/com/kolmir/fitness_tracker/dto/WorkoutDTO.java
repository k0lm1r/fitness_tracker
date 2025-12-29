package com.kolmir.fitness_tracker.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkoutDTO {
    @NotNull(message = "id категории не может быть пустым")
    @Positive(message = "id категории должен быть больше 0")
    private Long categoryId;

    @NotBlank(message = "название не может быть пустым")
    @Size(min = 1, max = 100, message = "длина названия должна быть от 1 до 100 символов")
    private String name;

    @NotNull(message = "поле даты тренировки не может быть пустым")
    @PastOrPresent(message = "дата тренировки не может быть в прошлом")
    private LocalDateTime workoutDate;

    @NotNull(message = "длительность должна содержать значение")
    @Positive(message = "длительность тренировки должна быть больше 0")
    private Integer durationMinutes;

    @NotNull(message = "поле числа потраченных калорий не может быть пустым")
    @Positive(message = "число потраченных калорий должно быть больше 0")
    private Integer calories;

    private Long ownerId;
}
