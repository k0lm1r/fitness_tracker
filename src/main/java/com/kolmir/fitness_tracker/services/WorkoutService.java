package com.kolmir.fitness_tracker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.workout.WorkoutRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutResponse;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.mappers.WorkoutMapper;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final ExerciseService exerciseService;
    private final WorkoutMapper workoutMapper;
    private final ExerciseMapper exerciseMapper;

    @Transactional
    public List<WorkoutResponse> getAll() {
        return workoutRepository.findAll().stream()
                .map(workoutMapper::toWorkoutResponse)
                .toList();
    }

    public WorkoutResponse saveWithoutTransactional(WorkoutRequest request) {
        Workout workout = workoutMapper.toWorkout(request);
        Set<Exercise> exercises = workout.getExercises();
        workout.setExercises(new HashSet<>());
        workout = workoutRepository.save(workout);
        
        for (var exercise : exercises) {
            exercise.getWorkouts().add(workout);
            workout.getExercises().add(exercise);
            exerciseService.update(exercise.getId(), exerciseMapper.toRequest(exercise));
        }
        
        exerciseService.invalidateCache();
        return workoutMapper.toWorkoutResponse(workoutRepository.save(workout));
    }

    @Transactional
    public WorkoutResponse saveWithTransactional(WorkoutRequest request) {
        return saveWithoutTransactional(request);
    }

    @Transactional
    public void delete(Long id) {
        if (workoutRepository.existsById(id)) {
            exerciseService.invalidateCache();
            workoutRepository.deleteById(id);
        } else 
            throw new NotFoundException("тренировка с таким id не найдена");
    }

    @Transactional(readOnly = true)
    public WorkoutResponse getById(Long id) {
        return workoutMapper.toWorkoutResponse(workoutRepository.findById(id).orElseThrow(
            () -> new NotFoundException("тренировка с таким id не найдена")
        ));
    }

    @Transactional
    public WorkoutResponse update(Long id, WorkoutRequest request) {
        if (!workoutRepository.existsById(id))
            throw new NotFoundException("тренировка с таким id не найдена");
        
        Workout workout = workoutMapper.toWorkout(request);
        workout.setId(id);
        return workoutMapper.toWorkoutResponse(workoutRepository.save(workout));
    }
}
