package com.kolmir.fitness_tracker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.kolmir.fitness_tracker.dto.workout.WorkoutSetRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutSetResponse;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.mappers.WorkoutMapper;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutSetService {
    private final WorkoutRepository workoutSetRepository;
    private final ExerciseService workoutService;
    private final WorkoutMapper workoutSetMapper;
    private final ExerciseMapper workoutMapper;

    @Transactional
    public List<WorkoutSetResponse> getAll() {
        return workoutSetRepository.findAll().stream()
                .map(workoutSetMapper::toWorkoutSetResponse)
                .toList();
    }

    public WorkoutSetResponse saveWithoutTransactional(WorkoutSetRequest request) throws WorkoutNotFoundException {
        Workout workoutSet = workoutSetMapper.toWorkoutSet(request);
        Set<Exercise> workouts = workoutSet.getExercises();
        workoutSet.setExercises(new HashSet<>());
        
        for (var w : workouts) {
            w.getWorkouts().add(workoutSet);
            workoutSet.getExercises().add(w);
            workoutService.update(w.getId(), workoutMapper.toDTO(w));
            workoutSetRepository.save(workoutSet);
        }
        
        return workoutSetMapper.toWorkoutSetResponse(workoutSetRepository.save(workoutSetMapper.toWorkoutSet(request)));
    }

    @Transactional
    public WorkoutSetResponse saveWithTransactional(WorkoutSetRequest request) throws WorkoutNotFoundException {
        return saveWithoutTransactional(request);
    }
}
