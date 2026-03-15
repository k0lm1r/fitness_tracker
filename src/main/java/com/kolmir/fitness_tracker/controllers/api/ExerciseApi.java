package com.kolmir.fitness_tracker.controllers.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Exercises", description = "Управление упражнениями")
@SecurityRequirement(name = "bearerAuth")
public interface ExerciseApi {
    @Operation(summary = "Получить упражнения с фильтрами и пагинацией")
    @ApiResponse(responseCode = "200", description = "Страница упражнений")
    Page<ExerciseResponse> getAllWithFilters(ExerciseFilter exerciseFilter, Pageable pageable);

    @Operation(summary = "Получить упражнение по id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Упражнение найдено"),
        @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ExerciseResponse> getById(Long id);

    @Operation(summary = "Создать упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Упражнение создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ExerciseResponse> create(ExerciseRequest exerciseDTO);

    @Operation(summary = "Обновить упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Упражнение обновлено"),
        @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ExerciseResponse> update(Long id, ExerciseRequest exerciseDTO);

    @Operation(summary = "Удалить упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Упражнение удалено"),
        @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(Long id);
}
