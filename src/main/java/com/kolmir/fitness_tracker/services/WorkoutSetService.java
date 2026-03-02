package com.kolmir.fitness_tracker.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.kolmir.fitness_tracker.dto.WorkoutSetRequest;
import com.kolmir.fitness_tracker.dto.WorkoutSetResponse;
import com.kolmir.fitness_tracker.mappers.WorkoutSetMapper;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.models.WorkoutSet;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.repository.WorkoutSetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutSetService {
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutSetMapper workoutSetMapper;

    @Transactional
    public List<WorkoutSetResponse> getAll() {
        return workoutSetRepository.findAll().stream()
                .map(workoutSetMapper::toWorkoutSetResponse)
                .toList();
    }

    public WorkoutSetResponse saveWithoutTransactional(WorkoutSetRequest request) {
        WorkoutSet workoutSet = workoutSetMapper.toWorkoutSet(request);
        Set<Workout> workouts = workoutSet.getWorkouts();
        workoutSet.setWorkouts(new HashSet<>());
        
        for (var w : workouts) {
            Set<WorkoutSet> sets = w.getWorkoutSets();
            sets.add(workoutSet);
            w.setWorkoutSets(sets);
            workoutSet.getWorkouts().add(w);
            workoutRepository.save(w);
            workoutSetRepository.save(workoutSet);
        }
        
        return workoutSetMapper.toWorkoutSetResponse(workoutSetRepository.save(workoutSetMapper.toWorkoutSet(request)));
    }

    @Transactional
    public WorkoutSetResponse saveWithTransactional(WorkoutSetRequest request) {
        return saveWithoutTransactional(request);
    }
}
