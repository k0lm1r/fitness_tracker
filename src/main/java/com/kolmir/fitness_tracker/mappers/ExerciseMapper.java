package com.kolmir.fitness_tracker.mappers;

import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseDTO;
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
    public abstract ExerciseDTO toDTO(Exercise exercise);

    public abstract Exercise toEntity(ExerciseDTO exerciseDTO);

    @AfterMapping
    protected void setCategory(ExerciseDTO exerciseDTO, @MappingTarget Exercise exercise) {
        if (exerciseDTO.getCategoryId() != null)
            exercise.setCategory(entityManager.getReference(Category.class, exerciseDTO.getCategoryId()));
    }

    @AfterMapping
    protected void setExerciseIds(Exercise exercise, @MappingTarget ExerciseDTO exerciseDTO) {
        if (exercise.getWorkouts() != null)
            exerciseDTO.setWorkoutIds(
                exercise.getWorkouts().stream()
                .map(Workout::getId)
                .collect(Collectors.toSet())
            );
    }
}
