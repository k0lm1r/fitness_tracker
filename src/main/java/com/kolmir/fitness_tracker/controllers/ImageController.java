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

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.entityToDTO(imageService.upload(file)));
    }
    
}
