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
    @Query("select d from Day d left join fetch d.workout where d.owner.id = :ownerId")
    public List<Day> findAllByOwnerId(@Param("ownerId") Long ownerId);
    public Optional<Day> findDayByDateAndOwnerId(LocalDate date, Long ownerId);
}
