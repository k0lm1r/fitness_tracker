package com.kolmir.fitness_tracker.cache;


import org.springframework.data.domain.Pageable;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;

public record ExerciseCacheKey (
    String methodName,
    ExerciseFilter filter,
    Pageable pageable
) {}
