package com.kolmir.fitness_tracker.controllers.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskCreateResponse;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskStatusResponse;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    ResponseEntity<ExerciseResponse> getById(@PathVariable Long id);

    @Operation(summary = "Создать упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Упражнение создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest exerciseDTO);

    @Operation(summary = "Обновить упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Упражнение обновлено"),
        @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ExerciseResponse> update(@PathVariable Long id, @Valid @RequestBody ExerciseRequest exerciseDTO);

    @Operation(summary = "Удалить упражнение")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Упражнение удалено"),
        @ApiResponse(responseCode = "404", description = "Упражнение не найдено",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(@PathVariable Long id);

    @Operation(summary = "Массовое создание упражнений")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Упражнения успешно созданы"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<List<ExerciseResponse>> bulkPost(@RequestBody List<@Valid ExerciseRequest> requests, @RequestParam Boolean withTransactional);

    @Operation(summary = "Запустить асинхронное массовое создание упражнений")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Задача принята в обработку"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AsyncTaskCreateResponse> startAsyncBulkPost(@RequestBody List<@Valid ExerciseRequest> requests);

    @Operation(summary = "Получить статус асинхронной задачи")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Статус задачи получен"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AsyncTaskStatusResponse> getAsyncTaskStatus(@PathVariable String taskId);
}
