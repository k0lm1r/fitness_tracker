package com.kolmir.fitness_tracker.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.Workout;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class WorkoutSpecifications {
    public static Specification<Workout> withFilter(WorkoutFilter workoutFilter) {
        return Specification.where(hasOwner(workoutFilter.getOwnerId()))
                .and(hasCategory(workoutFilter.getCategoryName()))
                .and(wasBetween(workoutFilter.getDateFrom(), workoutFilter.getDateTo()))
                .and(hasDuration(workoutFilter.getDurationMinutesFrom(), workoutFilter.getDurationMinutesTo()));
    }

    private static Specification<Workout> hasOwner(Long ownerId) {
        return (root, query, cb) -> {
            return cb.equal(root.get("owner").get("id"), ownerId);
        };
    }

    private static Specification<Workout> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName != null) {
                Join<Workout, Category> rootJoin = root.join("category");
                return cb.equal(rootJoin.get("name"), categoryName);
            }

            return cb.and();
        };
    }

    private static Specification<Workout> wasBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) 
                predicates.add(cb.greaterThanOrEqualTo(root.get("workoutDate"), from));
            if (to != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("workoutDate"), to));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Workout> hasDuration(Integer min, Integer max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (min != null) 
                predicates.add(cb.ge(root.get("durationMinutes"), min));
            if (max != null)
                predicates.add(cb.le(root.get("durationMinutes"), max));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
