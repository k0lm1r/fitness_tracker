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

import com.kolmir.fitness_tracker.controllers.api.WorkoutsControllerApi;
import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;
import com.kolmir.fitness_tracker.services.WorkoutService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workouts")
public class WorkoutsController implements WorkoutsControllerApi {
    private final WorkoutService workoutService;

    @GetMapping
    @Override
    public Page<WorkoutDTO> getAllWithFilters(WorkoutFilter workoutFilter, Pageable pageable) {
        workoutFilter.setOwnerId(CurrentUserProvider.getCurrentUserId());
        return workoutService.getAllByOwnerId(workoutFilter, pageable);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<WorkoutDTO> getById(@PathVariable Long id) throws WorkoutNotFoundException {
        return new ResponseEntity<>(workoutService.getById(id), HttpStatus.OK);
    }
    
    @PostMapping
    @Override
    public ResponseEntity<WorkoutDTO> create(@Valid @RequestBody WorkoutDTO workoutDTO) {
        return new ResponseEntity<>(workoutService.save(workoutDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<WorkoutDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody WorkoutDTO workoutDTO) throws WorkoutNotFoundException {
        return ResponseEntity.ok(workoutService.update(id, workoutDTO));
    }
    
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) throws WorkoutNotFoundException {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
