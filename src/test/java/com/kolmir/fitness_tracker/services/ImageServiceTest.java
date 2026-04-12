package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.kolmir.fitness_tracker.dto.image.ImageResponce;
import com.kolmir.fitness_tracker.exceptions.StorageException;
import com.kolmir.fitness_tracker.mappers.ImageMapper;
import com.kolmir.fitness_tracker.models.Image;
import com.kolmir.fitness_tracker.repository.ImageRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import io.minio.MinioClient;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private ImageService imageService;

    @Test
    void uploadSavesImageAndReturnsDto() throws Exception {
        ReflectionTestUtils.setField(imageService, "bucket", "test-bucket");
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "abc".getBytes());
        ImageResponce response = new ImageResponce();
        response.setOwnerId(3L);
        response.setPath("media/3/path");

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(3L);
            when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(imageMapper.toDTO(any(Image.class))).thenReturn(response);

            ImageResponce result = imageService.upload(file);

            assertEquals(3L, result.getOwnerId());
            assertEquals("media/3/path", result.getPath());
            verify(minioClient).putObject(any());
            verify(imageRepository).save(any(Image.class));
        }
    }

    @Test
    void uploadThrowsStorageExceptionWhenMinioFails() throws Exception {
        ReflectionTestUtils.setField(imageService, "bucket", "test-bucket");
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "abc".getBytes());

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(4L);
            when(minioClient.putObject(any())).thenThrow(new IOException("failed"));

            assertThrows(StorageException.class, () -> imageService.upload(file));
        }
    }
}
