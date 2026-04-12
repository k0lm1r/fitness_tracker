package com.kolmir.fitness_tracker.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "Ответ после загрузки изображения")
public class ImageResponce {
    @Schema(description = "Идентификатор изображения", example = "15")
    private Long id;

    @Schema(description = "Имя файла", example = "2026-03-16T10:15:30.png")
    private String filename;

    @Schema(description = "Путь к файлу в хранилище", example = "media/1/2026-03-16T10:15:30.png")
    private String path;

    @Schema(description = "Идентификатор владельца файла", example = "1")
    private Long ownerId;
}
