package com.kolmir.fitness_tracker.controllers.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;

import jakarta.validation.Valid;

@Tag(name = "Workouts", description = "Управление тренировками")
public interface WorkoutsControllerApi {

    @Operation(
            summary = "Получить тренировки с фильтрами и пагинацией",
            description = "Возвращает страницу тренировок текущего пользователя с поддержкой фильтров по категории, дате и длительности. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список тренировок", content = @Content(schema = @Schema(implementation = WorkoutDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтра", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    Page<WorkoutDTO> getAllWithFilters(@ParameterObject WorkoutFilter workoutFilter, @ParameterObject Pageable pageable);

    @Operation(
            summary = "Получить тренировку по id",
            description = "Возвращает тренировку текущего пользователя по идентификатору. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тренировка найдена", content = @Content(schema = @Schema(implementation = WorkoutDTO.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Тренировка не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<WorkoutDTO> getById(@PathVariable Long id) throws WorkoutNotFoundException;

    @Operation(
            summary = "Создать новую тренировку",
            description = "Создаёт тренировку и привязывает к текущему пользователю. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Тренировка создана", content = @Content(schema = @Schema(implementation = WorkoutDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WorkoutDTO.class),
                    examples = @ExampleObject(
                            name = "Пример тренировки",
                            value = """
                                    {
                                      "categoryId": 1,
                                      "name": "Утренний бег",
                                      "workoutDate": "2024-01-15T07:30:00",
                                      "durationMinutes": 45,
                                      "calories": 450
                                    }
                                    """
                    )))
    ResponseEntity<WorkoutDTO> create(@Valid @RequestBody WorkoutDTO workoutDTO);

    @Operation(
            summary = "Обновить тренировку по id",
            description = "Изменяет тренировку пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Тренировка обновлена", content = @Content(schema = @Schema(implementation = WorkoutDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Тренировка не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WorkoutDTO.class),
                    examples = @ExampleObject(
                            name = "Пример обновления",
                            value = """
                                    {
                                      "categoryId": 2,
                                      "name": "Силовая тренировка",
                                      "workoutDate": "2024-01-20T18:00:00",
                                      "durationMinutes": 60,
                                      "calories": 600
                                    }
                                    """
                    )))
    ResponseEntity<WorkoutDTO> update(@PathVariable Long id, @Valid @RequestBody WorkoutDTO workoutDTO) throws WorkoutNotFoundException;

    @Operation(
            summary = "Удалить тренировку по id",
            description = "Удаляет тренировку текущего пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Тренировка не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(@PathVariable Long id) throws WorkoutNotFoundException;
}
