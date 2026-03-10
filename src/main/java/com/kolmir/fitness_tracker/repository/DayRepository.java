package com.kolmir.fitness_tracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Day;


@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    @Query(value = "select d.* from days join workouts w on d.wokrout_id = w.id where workout.owner_id = :ownerId", nativeQuery = true)
    public List<Day> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("select d from Day d left join d.workout w where w.owner.id = :ownerId")
    public Optional<Day> findDayByDateAndOwnerId(LocalDate date, Long ownerId);
}
