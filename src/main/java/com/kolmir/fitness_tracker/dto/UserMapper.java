package com.kolmir.fitness_tracker.dto;

import org.mapstruct.Mapper;

import com.kolmir.fitness_tracker.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public User toEntity(UserRegisterRequest request);
}