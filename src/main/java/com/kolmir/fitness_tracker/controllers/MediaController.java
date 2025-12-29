package com.kolmir.fitness_tracker.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.controllers.api.MediaControllerApi;
import com.kolmir.fitness_tracker.services.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/media")
public class MediaController implements MediaControllerApi {

    private final ImageService imageService;
    
    @PostMapping
    @Override
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file ) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.upload(file));
    }
    
}
