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

import com.kolmir.fitness_tracker.dto.exercise.ExerciseDTO;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;
import com.kolmir.fitness_tracker.services.ExerciseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workouts")
public class ExerciseController {
    private final ExerciseService exericesService;

    @GetMapping
    public Page<ExerciseDTO> getAllWithFilters(ExerciseFilter workoutFilter, Pageable pageable) {
        workoutFilter.setOwnerId(CurrentUserProvider.getCurrentUserId());
        return exericesService.getAllByOwnerId(workoutFilter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getById(@PathVariable Long id) throws WorkoutNotFoundException {
        return new ResponseEntity<>(exericesService.getById(id), HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ExerciseDTO> create(@Valid @RequestBody ExerciseDTO workoutDTO) {
        return new ResponseEntity<>(exericesService.save(workoutDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody ExerciseDTO workoutDTO) throws WorkoutNotFoundException {
        return ResponseEntity.ok(exericesService.update(id, workoutDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws WorkoutNotFoundException {
        exericesService.delete(id);
        return ResponseEntity.noContent().build();
    }    
}
