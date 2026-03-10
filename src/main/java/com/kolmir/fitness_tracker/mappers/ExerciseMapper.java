package com.kolmir.fitness_tracker.mappers;

import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ExerciseMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "categoryId", source = "category.id")
    public abstract ExerciseResponse toResponse(Exercise exercise);

    @Mapping(target = "categoryId", source = "category.id")
    public abstract ExerciseRequest toRequest(Exercise exercise);

    public abstract Exercise toEntity(ExerciseRequest exerciseRequest);

    @AfterMapping
    protected void setCategory(ExerciseRequest exerciseRequest, @MappingTarget Exercise exercise) {
        if (exerciseRequest.getCategoryId() != null)
            exercise.setCategory(entityManager.getReference(Category.class, exerciseRequest.getCategoryId()));
    }

    @AfterMapping
    protected void setWorkoutIds(Exercise exercise, @MappingTarget ExerciseResponse exerciseResponse) {
        if (exercise.getWorkouts() != null)
            exerciseResponse.setWorkoutIds(
                exercise.getWorkouts().stream()
                .map(Workout::getId)
                .collect(Collectors.toSet())
            );
    }
}
