package com.kolmir.fitness_tracker.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kolmir.fitness_tracker.dto.CategoryDTO;
import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.Workout;

@Configuration
public class ModelMapperConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        
        modelMapper.typeMap(WorkoutDTO.class, Workout.class)
            .addMappings(map -> {   
                map.skip(Workout::setOwner);
                map.skip(Workout::setCategory);
            });
        modelMapper.typeMap(CategoryDTO.class, Category.class)
            .addMappings(map -> {
                map.skip(Category::setOwner);
            });
        
        return modelMapper;
    }
}
