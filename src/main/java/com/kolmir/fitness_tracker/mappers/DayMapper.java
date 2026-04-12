package com.kolmir.fitness_tracker.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.models.Day;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Mapper (
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class DayMapper {
    @PersistenceContext
    protected EntityManager entityManager;

    public abstract Day toDay(DayUpdateRequest request);
    public abstract Day toDay(DayCreateRequest request);
    public abstract DayResponse toResponse(Day day);

    @AfterMapping
    protected void setWorkoutId(Day day, @MappingTarget DayResponse response) {
        response.setWokroutId(day.getWorkout().getId());
    }

    @AfterMapping
    protected void setWorkout(DayCreateRequest request, @MappingTarget Day day) {
        day.setWorkout(entityManager.getReference(Workout.class, request.getWorkoutId()));
    }
}
