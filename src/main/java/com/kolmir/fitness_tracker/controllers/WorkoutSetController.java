package com.kolmir.fitness_tracker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.workout.WorkoutSetRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutSetResponse;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.services.WorkoutSetService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
@RequestMapping("/workout-sets")
public class WorkoutSetController {

    private final WorkoutSetService workoutSetService;

    @GetMapping
    public List<WorkoutSetResponse> getAll() {
        return workoutSetService.getAll();
    }

    @PostMapping
    public WorkoutSetResponse createWorkoutSet(@RequestBody WorkoutSetRequest request, 
                                                @RequestParam boolean withTransactional) throws WorkoutNotFoundException {
        return withTransactional ? workoutSetService.saveWithTransactional(request) : workoutSetService.saveWithoutTransactional(request);
    }
    
}
