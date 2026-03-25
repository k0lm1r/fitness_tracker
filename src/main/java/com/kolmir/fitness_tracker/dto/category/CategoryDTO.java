package com.kolmir.fitness_tracker.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "DTO категории упражнения")
public class CategoryDTO {
    @Schema(description = "Идентификатор категории", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Название категории", example = "Кардио")
    @NotBlank(message = "название категории не может быть пустым")
    @Size(max = 50, message = "название категории не может быть длинее 50 символов")
    private String name;

    @Schema(description = "Идентификатор владельца категории", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long ownerId;
}
