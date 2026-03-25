package com.kolmir.fitness_tracker.repository;

import java.util.List;

import com.kolmir.fitness_tracker.models.Exercise;

public interface CustomExerciseRepository {
    public List<Exercise> banchSave(List<Exercise> exercises);
}
