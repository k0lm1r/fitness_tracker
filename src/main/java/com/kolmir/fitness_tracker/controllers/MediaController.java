package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.dto.ImageResponce;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;
import com.kolmir.fitness_tracker.services.ImageService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

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
@RequestMapping("/media")
@Tag(name = "Media", description = "Загрузка изображений тренировок")
public class MediaController {

    private final ImageService imageService;
    
    @PostMapping
    @Operation(
            summary = "Загрузить изображение",
            description = "Принимает multipart/form-data с файлом изображения и возвращает информацию о сохранённом файле. Требует Bearer JWT.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Файл загружен", content = @Content(schema = @Schema(implementation = ImageResponce.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат файла", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(type = "string", format = "binary"),
            examples = @ExampleObject(name = "Пример запроса", value = "(binary image file)")))
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.entityToDTO(imageService.upload(file)));
    }
    
}
