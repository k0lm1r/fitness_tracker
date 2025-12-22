package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.services.CategoryService;
import com.kolmir.fitness_tracker.utils.ErrorResponse;
import com.kolmir.fitness_tracker.utils.category.CategoryNotFoundException;
import com.kolmir.fitness_tracker.utils.category.CategoryNotValidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Categories", description = "CRUD операции с категориями тренировок")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            summary = "Получить список категорий текущего пользователя",
            description = "Возвращает все категории для текущего пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список категорий", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<CategoryDTO> getAll(@AuthenticationPrincipal User user) {
        return categoryService.getAll(user.getId()).stream().map(categoryService::entityToDTO).toList();
    }
    
    @GetMapping("/{id}")
    @Operation(
            summary = "Получить категорию по id",
            description = "Возвращает категорию по идентификатору текущего пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория найдена", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> getById(@PathVariable Long id) throws CategoryNotFoundException {
        return ResponseEntity.ok(categoryService.entityToDTO(categoryService.getById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Создать новую категорию",
            description = "Создаёт категорию и привязывает её к текущему пользователю. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Категория создана", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Конфликт данных", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryDTO.class),
            examples = @ExampleObject(
                    name = "Новая категория",
                    value = """
                            {
                              "name": "Кардио"
                            }
                            """)))
    public ResponseEntity<?> create(@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult) throws CategoryNotValidException {
        if (bindingResult.hasErrors())
            throw new CategoryNotValidException(ErrorResponse.getExceptionMessage(bindingResult));

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.entityToDTO(categoryService.save(categoryService.DTOtoEntity(categoryDTO)))); 
    }
    
    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить категорию по id",
            description = "Обновляет название категории пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория обновлена", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CategoryDTO.class),
            examples = @ExampleObject(
                    name = "Обновление категории",
                    value = """
                            {
                              "name": "Силовые"
                            }
                            """)))
    public ResponseEntity<CategoryDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody CategoryDTO categoryDTO,
                    BindingResult bindingResult) throws CategoryNotValidException, CategoryNotFoundException {
        
        if (bindingResult.hasErrors()) 
            throw new CategoryNotValidException(ErrorResponse.getExceptionMessage(bindingResult));
        
        CategoryDTO updatedcategory = categoryService.entityToDTO(
                categoryService.update(id, categoryService.DTOtoEntity(categoryDTO)));
        return ResponseEntity.ok(updatedcategory);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить категорию по id",
            description = "Удаляет категорию пользователя. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) throws CategoryNotFoundException {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
