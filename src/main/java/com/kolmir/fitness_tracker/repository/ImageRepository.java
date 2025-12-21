package com.kolmir.fitness_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.fitness_tracker.models.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
