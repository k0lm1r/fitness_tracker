package com.kolmir.fitness_tracker.models;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
@Table(name = "workout_sets")
public class WorkoutSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable (
        name = "set_items",
        joinColumns = @JoinColumn(name = "workout_set_id"),
        inverseJoinColumns = @JoinColumn(name = "workout_id"),
        uniqueConstraints = @UniqueConstraint (
            columnNames = {"workout_set_id", "workout_id"}
        )
    )
    Set<Workout> workouts;
}
