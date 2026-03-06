package com.kolmir.fitness_tracker.dto.workout;

import java.util.Set;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseDTO;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class WorkoutSetResponse {
    private String name;
    private Long ownerId;
    private Set<ExerciseDTO> workouts;
}
