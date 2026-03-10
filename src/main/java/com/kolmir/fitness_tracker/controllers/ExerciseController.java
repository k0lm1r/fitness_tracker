package com.kolmir.fitness_tracker.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;
import com.kolmir.fitness_tracker.services.ExerciseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public Page<ExerciseResponse> getAllWithFilters(ExerciseFilter exerciseFilter, Pageable pageable) {
        exerciseFilter.setOwnerId(CurrentUserProvider.getCurrentUserId());
        return exerciseService.getAllByOwnerId(exerciseFilter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id) throws WorkoutNotFoundException {
        return new ResponseEntity<>(exerciseService.getById(id), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest exerciseDTO) {
        return new ResponseEntity<>(exerciseService.save(exerciseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody ExerciseRequest exerciseDTO) throws WorkoutNotFoundException {
        return ResponseEntity.ok(exerciseService.update(id, exerciseDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws WorkoutNotFoundException {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }    
}
