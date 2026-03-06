package com.kolmir.fitness_tracker.dto.exercise;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExerciseFilter {
    private String name;
    private Long categoryId;
    private String categoryName;
    private Long ownerId;
    private Integer durationMinutesFrom;
    private Integer durationMinutesTo;
}
