package com.kolmir.fitness_tracker.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kolmir.fitness_tracker.dto.ImageMapper;
import com.kolmir.fitness_tracker.dto.ImageResponce;
import com.kolmir.fitness_tracker.models.Image;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.ImageRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public ImageResponce upload(MultipartFile file) throws Exception {
        Long currentUserId = CurrentUserProvider.getCurrentUserId();
        String filename = LocalDateTime.now() + ".png";
        Image image = new Image();
        User user = new User();
        
        user.setId(currentUserId);
        image.setFilename(filename);
        image.setPath("media/" + currentUserId + "/" + filename);
        image.setOwner(user);

        minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

        return imageMapper.toDTO(imageRepository.save(image));
    }
}
