package com.kolmir.fitness_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Workout;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long>, 
                JpaSpecificationExecutor<Workout> {
    
    public boolean existsByIdAndOwnerId(Long id, Long ownerId);

    @EntityGraph(attributePaths = {
        "workoutSets",
        "workoutSets.workouts",
    })
    @Query("select w from Workout w")
    public List<Workout> findAllWithSets();
}
