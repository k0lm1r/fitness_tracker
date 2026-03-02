package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class WorkoutMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract WorkoutDTO toDTO(Workout workout);

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