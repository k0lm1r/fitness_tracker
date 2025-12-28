package com.kolmir.fitness_tracker.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Mapper(componentModel = "spring")
public abstract class WorkoutMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract WorkoutDTO toDTO(Workout workout);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "category", ignore = true)
    public abstract Workout toEntity(WorkoutDTO workoutDTO);

    @AfterMapping
    protected void setOwner(WorkoutDTO workoutDTO, @MappingTarget Workout workout) {
        if (workoutDTO.getOwnerId() != null)
            workout.setOwner(entityManager.getReference(User.class, workoutDTO.getOwnerId()));
    }

    @AfterMapping
    protected void setCategory(WorkoutDTO workoutDTO, @MappingTarget Workout workout) {
        if (workoutDTO.getCategoryId() != null)
            workout.setCategory(entityManager.getReference(Category.class, workoutDTO.getCategoryId()));
    }
}