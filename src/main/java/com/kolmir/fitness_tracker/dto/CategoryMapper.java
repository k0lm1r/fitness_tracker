package com.kolmir.fitness_tracker.dto;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    @Mapping(target = "ownerId", source = "owner.id")
    public abstract CategoryDTO toDTO(Category category);

    @Mapping(target = "owner", ignore = true)
    public abstract Category toEntity(CategoryDTO categoryDTO);

    @AfterMapping
    protected void setOwner(CategoryDTO categoryDTO, @MappingTarget Category category) {
        if (categoryDTO.getOwnerId() != null)
            category.setOwner(entityManager.getReference(User.class, categoryDTO.getOwnerId()));
    }
}
