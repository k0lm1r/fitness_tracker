package com.kolmir.fitness_tracker.dto.workout;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class WorkoutRequest {
    @NotBlank
    @Size(min = 1, max = 100, message = "workout name should be from 1 to 100 characters")
    private String name;

    private Set<Long> exerciseIds;
    private Long ownerId;
}
