package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.services.WorkoutService;
import com.kolmir.fitness_tracker.utils.ErrorResponse;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotValidException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workouts")
public class WorkoutsController {
    private final WorkoutService workoutService;

    @GetMapping
    public List<WorkoutDTO> index() {
        //TODO Test realisation
        return workoutService.getAllByOwnerId().stream()
            .map(w -> workoutService.entityToDTO(w))
            .toList();
    }

    @GetMapping("/{id}")
    public WorkoutDTO getById(@PathVariable Long id) throws WorkoutNotFoundException {
        return workoutService.entityToDTO(workoutService.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<WorkoutDTO> create(
                    @Valid @RequestBody WorkoutDTO workoutDTO, 
                    BindingResult bindingResult) throws WorkoutNotValidException {
        
        if (bindingResult.hasErrors())
            throw new WorkoutNotValidException(ErrorResponse.getExceptionMessage(bindingResult));
        WorkoutDTO createdWorkout = workoutService.entityToDTO(
                workoutService.save(workoutService.DTOtoEntity(workoutDTO)));
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutDTO> update(
                    @PathVariable Long id, 
                    @Valid @RequestBody WorkoutDTO workoutDTO,
                    BindingResult bindingResult) throws WorkoutNotValidException {
        
        if (bindingResult.hasErrors()) 
            throw new WorkoutNotValidException(ErrorResponse.getExceptionMessage(bindingResult));
        
        WorkoutDTO updatedWorkout = workoutService.entityToDTO(
                workoutService.save(workoutService.DTOtoEntity(workoutDTO)));
        return ResponseEntity.ok(updatedWorkout);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws WorkoutNotFoundException {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
