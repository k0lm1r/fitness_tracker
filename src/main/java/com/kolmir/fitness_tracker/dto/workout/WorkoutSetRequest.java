package com.kolmir.fitness_tracker.dto.workout;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class WorkoutSetRequest {
    @NotBlank
    @Size(min = 1, max = 100, message = "workout sets name should be from 1 to 100 characters")
    private String name;

    private Set<Long> workoutIds;
    private Long ownerId;
}
