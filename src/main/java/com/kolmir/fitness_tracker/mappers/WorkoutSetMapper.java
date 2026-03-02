package com.kolmir.fitness_tracker.mappers;

import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.WorkoutSetRequest;
import com.kolmir.fitness_tracker.dto.WorkoutSetResponse;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.models.WorkoutSet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper(
    componentModel = "spring",
    uses = WorkoutMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class WorkoutSetMapper {

    @PersistenceContext
    protected EntityManager entityManager;
    
    public abstract WorkoutSet toWorkoutSet(WorkoutSetRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    public abstract WorkoutSetResponse toWorkoutSetResponse(WorkoutSet workoutSet);

    @AfterMapping
    protected void setOwnerAndWorkouts(WorkoutSetRequest request, @MappingTarget WorkoutSet workoutSet) {
        workoutSet.setOwner(entityManager.getReference(User.class, request.getOwnerId()));
        workoutSet.setWorkouts(request.getWorkoutIds().stream()
            .map(workoutId -> entityManager.getReference(Workout.class, workoutId))
            .collect(Collectors.toSet())
        );
    }
}
