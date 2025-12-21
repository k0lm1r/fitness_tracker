package com.kolmir.fitness_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryDTO {

    @NotBlank(message = "название категории не может быть пустым")
    private String name;

    private Long ownerId;
}
