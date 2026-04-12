package com.kolmir.fitness_tracker.mappers;

import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.workout.WorkoutRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutResponse;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper(
    componentModel = "spring",
    uses = ExerciseMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class WorkoutMapper {

    @PersistenceContext
    protected EntityManager entityManager;
    
    public abstract Workout toWorkout(WorkoutRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    public abstract WorkoutResponse toWorkoutResponse(Workout workout);

    @AfterMapping
    protected void setExercises(WorkoutRequest request, @MappingTarget Workout workout) {
        if (request.getExerciseIds() != null)
            workout.setExercises(request.getExerciseIds().stream()
                .map(exerciseId -> entityManager.getReference(Exercise.class, exerciseId))
                .collect(Collectors.toSet())
            );
    }

    @AfterMapping
    protected void setExerciseIds(Workout workout, @MappingTarget WorkoutResponse response) {
        if (workout.getExercises() != null)
            response.setExerciseIds(
                workout.getExercises().stream()
                .map(e -> e.getId())
                .collect(Collectors.toSet())
            );
    }
}
