package com.kolmir.fitness_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Workout;


@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    @EntityGraph(attributePaths = {
        "exercises",
        "days"
    })
    public List<Workout> findAll();
}