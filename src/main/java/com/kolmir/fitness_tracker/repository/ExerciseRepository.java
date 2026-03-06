package com.kolmir.fitness_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Exercise;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>, 
                JpaSpecificationExecutor<Exercise> {
    
    public boolean existsByIdAndOwnerId(Long id, Long ownerId);

    @EntityGraph(attributePaths = {
        "workoutSets",
        "workoutSets.workouts",
    })
    @Query("select w from Workout w")
    public List<Exercise> findAllWithSets();
}
