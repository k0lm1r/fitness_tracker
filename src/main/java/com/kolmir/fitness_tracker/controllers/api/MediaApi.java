package com.kolmir.fitness_tracker.controllers.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.dto.image.ImageResponce;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Media", description = "Загрузка медиафайлов")
@SecurityRequirement(name = "bearerAuth")
public interface MediaApi {
    @Operation(summary = "Загрузить изображение")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Файл загружен"),
        @ApiResponse(responseCode = "500", description = "Ошибка загрузки файла",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ImageResponce> uploadImage(MultipartFile file);
}
