package com.kolmir.fitness_tracker.models;

import java.time.DayOfWeek;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;


@Data
@Entity
@Table(name = "workouts")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    private User owner;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable (
        name = "workout_exercises",
        joinColumns = @JoinColumn(name = "workout_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id"),
        uniqueConstraints = @UniqueConstraint (
            columnNames = {"workout_id", "exercise_id"}
        )
    )
    Set<Exercise> exercises;

    @ElementCollection
    @CollectionTable (
        name = "days_of_week",
        joinColumns = @JoinColumn(name = "workout_id")
    )
    @Column(name = "day_name")
    @Enumerated(EnumType.STRING)
    Set<DayOfWeek> workoutDays;
}
