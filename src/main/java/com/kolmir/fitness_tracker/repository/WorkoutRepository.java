package com.kolmir.fitness_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Workout;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    
}
