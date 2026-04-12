package com.kolmir.fitness_tracker.controllers.api;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.workout.WorkoutRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutResponse;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Workouts", description = "Управление тренировками")
@SecurityRequirement(name = "bearerAuth")
public interface WorkoutApi {
    @Operation(summary = "Получить все тренировки")
    @ApiResponse(responseCode = "200", description = "Список тренировок")
    List<WorkoutResponse> getAll();

    @Operation(summary = "Получить тренировку по id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тренировка найдена"),
        @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutResponse> getById(Long id);

    @Operation(summary = "Создать тренировку")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тренировка создана"),
        @ApiResponse(responseCode = "404", description = "Связанная сущность не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutResponse> createWorkout(WorkoutRequest request, boolean withTransactional);

    @Operation(summary = "Обновить тренировку")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тренировка обновлена"),
        @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutResponse> update(Long id, WorkoutRequest request);

    @Operation(summary = "Удалить тренировку")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Тренировка удалена"),
        @ApiResponse(responseCode = "404", description = "Тренировка не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutResponse> delete(Long id);
}
