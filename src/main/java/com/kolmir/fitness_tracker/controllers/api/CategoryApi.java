package com.kolmir.fitness_tracker.controllers.api;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.category.CategoryDTO;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categories", description = "Управление категориями упражнений")
@SecurityRequirement(name = "bearerAuth")
public interface CategoryApi {
    @Operation(summary = "Получить все категории пользователя")
    @ApiResponse(responseCode = "200", description = "Список категорий")
    List<CategoryDTO> getAll();

    @Operation(summary = "Получить категорию по id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Категория найдена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CategoryDTO> getById(Long id);

    @Operation(summary = "Создать категорию")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Категория создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CategoryDTO> create(CategoryDTO categoryDTO);

    @Operation(summary = "Обновить категорию")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Категория обновлена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<CategoryDTO> update(Long id, CategoryDTO categoryDTO);

    @Operation(summary = "Удалить категорию")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Категория удалена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> delete(Long id);
}
