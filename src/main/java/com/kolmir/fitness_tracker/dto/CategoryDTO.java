package com.kolmir.fitness_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryDTO {

    @NotBlank(message = "название категории не может быть пустым")
    @Size(max = 50, message = "название категории не может быть длинее 50 символов")
    private String name;

    private Long ownerId;
}
