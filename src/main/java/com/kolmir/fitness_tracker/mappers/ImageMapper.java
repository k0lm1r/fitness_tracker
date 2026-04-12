package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.kolmir.fitness_tracker.dto.image.ImageResponce;
import com.kolmir.fitness_tracker.models.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    public ImageResponce toDTO(Image image);
}
