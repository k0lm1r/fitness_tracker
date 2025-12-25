package com.kolmir.fitness_tracker.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.dto.ImageResponce;
import com.kolmir.fitness_tracker.models.Image;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final String UPLOAD_DIR = "images/";
    private final ModelMapper modelMapper;

    public Image upload(MultipartFile file) throws IOException {
        String filename = saveImage(file);
        Image image = new Image();
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        image.setFilename(filename);
        image.setPath(Path.of(UPLOAD_DIR, filename).toString());
        image.setOwner(user);

        return imageRepository.save(image);
    }

    private void createUploadDirIfNotExists() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists())
            uploadDir.mkdirs();
    }

    private String saveImage(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        String filename = UUID.randomUUID() + ".png";
    
        createUploadDirIfNotExists();
        Files.write(Path.of(UPLOAD_DIR, filename), imageBytes);

        return filename;
    }

    public ImageResponce entityToDTO(Image image) {
        ImageResponce imageDTO = modelMapper.map(image, ImageResponce.class);
        imageDTO.setOwnerId(image.getOwner().getId());
        return imageDTO;
    }
}
