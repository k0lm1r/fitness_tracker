package com.kolmir.fitness_tracker.dto.day;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Schema(description = "Запрос на создание тренировочного дня")
public class DayCreateRequest {
    @Schema(description = "Идентификатор тренировки", example = "10")
    @NotNull(message = "ид тренировки не может быть пустым")
    @Positive(message = "ид тренировки должен быть больше 0")
    private Long workoutId;

    @Schema(description = "Дата тренировки", example = "2026-03-16")
    @NotNull(message = "поле даты тренировки не может быть пустым")
    @PastOrPresent(message = "дата тренировки не может быть в будущем")
    private LocalDate date;

    @Schema(description = "Количество потраченных калорий", example = "550")
    @NotNull(message = "поле числа потраченных калорий не может быть пустым")
    @Positive(message = "число потраченных калорий должно быть больше 0")
    private Integer calories;
}
