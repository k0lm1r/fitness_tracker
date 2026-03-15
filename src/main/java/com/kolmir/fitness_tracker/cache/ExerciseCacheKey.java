package com.kolmir.fitness_tracker.cache;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;

public record ExerciseCacheKey (
    String methodName,
    ExerciseFilter filter
) {}
