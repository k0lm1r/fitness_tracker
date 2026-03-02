package com.kolmir.fitness_tracker.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class WorkoutSetResponse {
    private String name;
    private Long ownerId;
    private Set<WorkoutDTO> workouts;
}
