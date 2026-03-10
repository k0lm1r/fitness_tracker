package com.kolmir.fitness_tracker.dto.exercise;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExerciseResponse {
    private Long id;
    private Long categoryId;
    private String name;
    private Integer durationMinutes;
    private Set<Long> workoutIds;
}
