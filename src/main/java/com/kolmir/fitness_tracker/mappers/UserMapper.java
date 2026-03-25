package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.user.UserRegisterRequest;
import com.kolmir.fitness_tracker.models.User;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    public User toEntity(UserRegisterRequest request);
}