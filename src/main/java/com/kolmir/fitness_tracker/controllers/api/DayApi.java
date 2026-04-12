package com.kolmir.fitness_tracker.controllers.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Days", description = "Календарь тренировочных дней")
@SecurityRequirement(name = "bearerAuth")
public interface DayApi {
    @Operation(summary = "Получить все дни пользователя")
    @ApiResponse(responseCode = "200", description = "Список дней")
    List<DayResponse> getAll(
            @Parameter(description = "Фильтр по названию тренировки")
            String workoutName
    );

    @Operation(summary = "Получить день по дате")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "День найден"),
        @ApiResponse(responseCode = "404", description = "День не найден",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DayResponse> getByDate(LocalDate date);

    @Operation(summary = "Создать запись дня")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "День создан"),
        @ApiResponse(responseCode = "409", description = "Запись на дату уже существует",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DayResponse> createDay(DayCreateRequest request);

    @Operation(summary = "Обновить запись дня")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "День обновлён"),
        @ApiResponse(responseCode = "404", description = "День не найден",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DayResponse> updateDay(LocalDate date, DayUpdateRequest request);

    @Operation(summary = "Удалить запись дня")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "День удалён"),
        @ApiResponse(responseCode = "404", description = "День не найден",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteDay(Long id);
}
