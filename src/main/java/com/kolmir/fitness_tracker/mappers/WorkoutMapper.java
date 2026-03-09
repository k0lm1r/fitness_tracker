package com.kolmir.fitness_tracker.mappers;

import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.dto.workout.WorkoutSetRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutSetResponse;
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
    
    public abstract Workout toWorkoutSet(WorkoutSetRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    public abstract WorkoutSetResponse toWorkoutSetResponse(Workout workoutSet);

    @AfterMapping
    protected void setOwnerAndWorkouts(WorkoutSetRequest request, @MappingTarget Workout workoutSet) {
        workoutSet.setOwner(entityManager.getReference(User.class, request.getOwnerId()));
        workoutSet.setExercises(request.getWorkoutIds().stream()
            .map(workoutId -> entityManager.getReference(Exercise.class, workoutId))
            .collect(Collectors.toSet())
        );
    }

    @AfterMapping
    protected void setWorkoutIds(Workout workout, @MappingTarget WorkoutSetResponse response) {
        if (workout.getExercises() != null)
            response.setExerciseIds(
                workout.getExercises().stream()
                .map(e -> e.getId())
                .collect(Collectors.toSet())
            );
    }
}
