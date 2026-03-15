package com.kolmir.fitness_tracker.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;


@Component
public class ExerciseCache {
    Map<ExerciseCacheKey, Page<ExerciseResponse>> cache = new HashMap<>();

    public Page<ExerciseResponse> put(ExerciseCacheKey key, Page<ExerciseResponse> value) {
        return cache.put(key, value);
    }

    public Page<ExerciseResponse> get(ExerciseCacheKey key) {
        return cache.get(key);
    }

    public void invalidate() {
        cache.clear();
    }

    public boolean containsKey(ExerciseCacheKey key) {
        return cache.containsKey(key);
    }
}
