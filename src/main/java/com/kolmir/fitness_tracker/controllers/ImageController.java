package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.services.ImageService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Tag(name = "Images", description = "Загрузка изображений тренировок")
public class ImageController {

    private final ImageService imageService;
    
    @PostMapping
    @Operation(summary = "Загрузить изображение", description = "Принимает multipart файл и возвращает метаданные")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.entityToDTO(imageService.upload(file)));
    }
    
}
