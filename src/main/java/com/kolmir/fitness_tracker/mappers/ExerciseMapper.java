package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Exercise;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ExerciseMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract ExerciseDTO toDTO(Exercise exercise);

    public abstract Exercise toEntity(ExerciseDTO exerciseDTO);

    @AfterMapping
    protected void setOwnerAndCategory(ExerciseDTO exerciseDTO, @MappingTarget Exercise exercise) {
        if (exerciseDTO.getOwnerId() != null)
            exercise.setOwner(entityManager.getReference(User.class, exerciseDTO.getOwnerId()));
        if (exerciseDTO.getCategoryId() != null)
            exercise.setCategory(entityManager.getReference(Category.class, exerciseDTO.getCategoryId()));
    }
}
