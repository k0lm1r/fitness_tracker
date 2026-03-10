package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.workout.WorkoutRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutResponse;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.services.WorkoutService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    public List<WorkoutResponse> getAll() {
        return workoutService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getById(@PathVariable Long id) throws WorkoutNotFoundException {
        return ResponseEntity.ok(workoutService.getById(id));
    }

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@RequestBody WorkoutRequest request, 
                                                @RequestParam boolean withTransactional) throws WorkoutNotFoundException {
        return ResponseEntity.ok(withTransactional ? workoutService.saveWithTransactional(request) : workoutService.saveWithoutTransactional(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> update(@PathVariable Long id, @RequestBody WorkoutRequest request) throws WorkoutNotFoundException {
        return ResponseEntity.ok(workoutService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WorkoutResponse> delete(@PathVariable Long id) throws WorkoutNotFoundException {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
