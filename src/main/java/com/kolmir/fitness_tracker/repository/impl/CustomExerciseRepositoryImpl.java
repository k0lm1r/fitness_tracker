package com.kolmir.fitness_tracker.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.repository.CustomExerciseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


public class CustomExerciseRepositoryImpl implements CustomExerciseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    public List<Exercise> banchSave(List<Exercise> exercises) {
        List<Exercise> response = new ArrayList<>();
        List<Exercise> persistense = new ArrayList<>();

        for (int i = 0; i < exercises.size(); ++i) {
            entityManager.persist(exercises.get(i));
            persistense.add(exercises.get(i));

            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                response.addAll(persistense);
                persistense.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();
        response.addAll(persistense);

        return response;
    }
}
