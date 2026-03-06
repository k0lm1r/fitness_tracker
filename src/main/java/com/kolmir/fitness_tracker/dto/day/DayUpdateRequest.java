package com.kolmir.fitness_tracker.dto.day;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class DayUpdateRequest {
    @NotNull(message = "поле числа потраченных калорий не может быть пустым")
    @Positive(message = "число потраченных калорий должно быть больше 0")
    private Integer calories;

    @NotNull(message = "ид тренировки не может быть пустым")
    @Positive(message = "ид тренировки должен быть больше 0")
    private Long workoutId;

    @NotNull(message = "поле даты тренировки не может быть пустым")
    @PastOrPresent(message = "дата тренировки не может быть в будущем")
    private LocalDate date;
}
