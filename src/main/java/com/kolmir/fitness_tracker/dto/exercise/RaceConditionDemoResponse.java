package com.kolmir.fitness_tracker.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Результат демонстрации race condition и потокобезопасного решения")
public class RaceConditionDemoResponse {
    @Schema(description = "Количество потоков", example = "50")
    private int threadCount;

    @Schema(description = "Инкрементов на поток", example = "2000")
    private int incrementsPerThread;

    @Schema(description = "Ожидаемое итоговое значение счётчика", example = "100000")
    private int expected;

    @Schema(description = "Значение небезопасного счётчика (возможна потеря инкрементов)", example = "94217")
    private int unsafeCounter;

    @Schema(description = "Значение безопасного Atomic-счётчика", example = "100000")
    private int atomicCounter;

    @Schema(description = "Количество потерянных инкрементов небезопасного счётчика", example = "5783")
    private int lostUpdates;

    @Schema(description = "Была ли зафиксирована гонка на небезопасном счётчике")
    private boolean raceDetected;

    @Schema(description = "Количество попыток запуска демонстрации", example = "2")
    private int attempts;
}
