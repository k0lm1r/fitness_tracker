package com.kolmir.fitness_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Workout;
import java.time.LocalDateTime;


@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    @Query("SELECT w FROM Workout w WHERE w.category.name = :name")
    public List<Workout> findByCategoryName(@Param("name") String name);

    @Query("SELECT w FROM Workout w WHERE w.owner.id = :ownerId")
    public List<Workout> findAllByOwnerId(@Param("ownerId") Long ownerId);

    public List<Workout> findByDurationMinutesBetween(@Param("min") Integer min, @Param("max") Integer max);

    public List<Workout> findAllByOrderByWorkoutDateAsc();
    public List<Workout> findAllByOrderByWorkoutDateDesc();

    public List<Workout> findAllByOrderByCaloriesAsc();
    public List<Workout> findAllByOrderByCaloriesDesc();

    @Query("SELECT w FROM Workout w WHERE " + 
        "(:start IS NULL OR w.workoutDate >= :start) " +
        "AND (:end IS NULL OR w.workoutDate <= :end)")
    public List<Workout> findAllByWorkoutDate(@Param("start") LocalDateTime start, 
                                                @Param("end") LocalDateTime end);

    public List<Workout> findByOwnerId(Long ownerId);
}
