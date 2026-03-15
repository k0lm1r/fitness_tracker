package com.kolmir.fitness_tracker.dto.day;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@Schema(description = "Ответ с данными тренировочного дня")
public class DayResponse {
    @Schema(description = "Дата тренировки", example = "2026-03-16")
    private LocalDate date;

    @Schema(description = "Идентификатор тренировки", example = "10")
    private Long wokroutId;

    @Schema(description = "Количество потраченных калорий", example = "550")
    private Integer calories;
}
