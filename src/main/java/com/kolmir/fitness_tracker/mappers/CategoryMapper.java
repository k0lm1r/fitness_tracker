package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.category.CategoryDTO;
import com.kolmir.fitness_tracker.models.Category;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CategoryMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    public abstract CategoryDTO toDTO(Category category);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Category toEntity(CategoryDTO categoryDTO);
}
