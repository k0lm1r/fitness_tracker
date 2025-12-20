package com.kolmir.fitness_tracker.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorkoutFilter {
    private String categoryName;
    private Long ownerId;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Integer durationMinutesFrom;
    private Integer durationMinutesTo;
}
