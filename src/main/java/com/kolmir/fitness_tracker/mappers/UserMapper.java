package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.Mapper;

import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toEntity(UserRegisterRequest request);
}