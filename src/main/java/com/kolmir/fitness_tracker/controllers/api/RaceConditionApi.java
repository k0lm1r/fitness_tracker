package com.kolmir.fitness_tracker.controllers.api;

import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.exercise.RaceConditionDemoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Concurrency", description = "Демонстрация проблем многопоточности")
@SecurityRequirement(name = "bearerAuth")
public interface RaceConditionApi {
    @Operation(summary = "Демонстрация race condition и решения через AtomicInteger")
    @ApiResponse(responseCode = "200", description = "Результат демонстрации гонки потоков")
    ResponseEntity<RaceConditionDemoResponse> raceConditionDemo();
}
