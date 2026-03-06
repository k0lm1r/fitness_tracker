package com.kolmir.fitness_tracker.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.Exercise;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExerciseSpecifications {

    public static Specification<Exercise> withFilter(ExerciseFilter filter) {
        if (filter == null) {
            filter = new ExerciseFilter();
        }
        return Specification.where(hasOwner(filter.getOwnerId()))
                .and(hasCategory(filter.getCategoryId(), filter.getCategoryName()))
                .and(hasName(filter.getName()))
                .and(hasDuration(filter.getDurationMinutesFrom(), filter.getDurationMinutesTo()));
    }

    private static Specification<Exercise> hasOwner(Long ownerId) {
        return (root, query, cb) -> {
            if (ownerId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("owner").get("id"), ownerId);
        };
    }

    private static Specification<Exercise> hasCategory(Long categoryId, String categoryName) {
        return (root, query, cb) -> {
            if (categoryId == null && !StringUtils.hasText(categoryName)) {
                return cb.conjunction();
            }

            Join<Exercise, Category> categoryJoin = root.join("category");
            Predicate predicate = cb.conjunction();

            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(categoryJoin.get("id"), categoryId));
            }
            if (StringUtils.hasText(categoryName)) {
                predicate = cb.and(predicate, cb.like(
                        cb.lower(categoryJoin.get("name")),
                        "%" + categoryName.trim().toLowerCase() + "%"));
            }

            return predicate;
        };
    }

    private static Specification<Exercise> hasName(String name) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(name)) {
                return cb.conjunction();
            }

            return cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
        };
    }

    private static Specification<Exercise> hasDuration(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return cb.conjunction();
            }

            Predicate predicate = cb.conjunction();

            if (min != null) {
                predicate = cb.and(predicate, cb.ge(root.get("durationMinutes"), min));
            }
            if (max != null) {
                predicate = cb.and(predicate, cb.le(root.get("durationMinutes"), max));
            }

            return predicate;
        };
    }
}
