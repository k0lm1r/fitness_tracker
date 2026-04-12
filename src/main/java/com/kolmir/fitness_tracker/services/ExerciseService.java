package com.kolmir.fitness_tracker.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.cache.ExerciseCacheKey;
import com.kolmir.fitness_tracker.cache.ExerciseCache;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskCreateResponse;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskStatusResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.models.AsyncTaskState;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.repository.ExerciseRepository;
import com.kolmir.fitness_tracker.repository.ExerciseSpecifications;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ApplicationContext applicationContext;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseCache cache;
    private final ConcurrentMap<String, AsyncTaskState> asyncTasks = new ConcurrentHashMap<>();
    private final AtomicInteger completedAsyncTasks = new AtomicInteger(0);

    @Transactional(readOnly = true)
    public Page<ExerciseResponse> getAllByOwnerId(ExerciseFilter exerciseFilter, Pageable pageable) {
        ExerciseCacheKey key = new ExerciseCacheKey("getByOwnerId", exerciseFilter, pageable);

        if (cache.containsKey(key))
            return cache.get(key);

        Specification<Exercise> specification = ExerciseSpecifications.withFilter(exerciseFilter);
        Page<ExerciseResponse> allResponses = exerciseRepository.findAll(specification, pageable).map(exerciseMapper::toResponse);
        cache.put(key, allResponses);
        return allResponses;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseResponse getById(Long id) {
        return exerciseMapper.toResponse(exerciseRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Упражнение с таким id не найдено.")
        ));
    }

    @Transactional
    public ExerciseResponse save(ExerciseRequest exerciseRequest) {
        Exercise exercise = exerciseMapper.toEntity(exerciseRequest);
        invalidateCache();
        return exerciseMapper.toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseResponse update(Long id, ExerciseRequest exerciseRequest) {
        Exercise exercise = exerciseMapper.toEntity(exerciseRequest);
        exercise.setId(id);

        if (!exerciseRepository.existsById(id))
            throw new NotFoundException("невозможно обновить несуществующее упражнение");

        invalidateCache();
        return exerciseMapper.toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) {
        if (!exerciseRepository.existsById(id))
            throw new NotFoundException("невозможно удалить несуществующее упражнение");
        invalidateCache();
        exerciseRepository.deleteById(id);
    }

    void invalidateCache() {
        cache.invalidate();
    }

    public List<ExerciseResponse> saveAllWithoutTransactional(List<ExerciseRequest> requests) {
        List<Exercise> exercises = requests.stream()
                                    .map(exerciseMapper::toEntity)
                                    .toList();
        List<ExerciseResponse> responses = new ArrayList<>();
        invalidateCache();

        for (var exercise : exercises) {
            exerciseRepository.save(exercise);
            responses.add(exerciseMapper.toResponse(exercise));
        }
        
        return responses;
    }
    
    
    @Transactional
    public List<ExerciseResponse> saveAllWithTransactional(List<ExerciseRequest> requests) {
        List<Exercise> exercises = requests.stream()
                                    .map(exerciseMapper::toEntity)
                                    .toList();
        List<ExerciseResponse> responses = exerciseRepository.banchSave(exercises).stream()
                .map(exerciseMapper::toResponse)
                .toList();
        invalidateCache();
        return responses;
    }

    public AsyncTaskCreateResponse startBulkSaveTask(List<ExerciseRequest> requests) {
        String taskId = UUID.randomUUID().toString();
        AsyncTaskState state = new AsyncTaskState(taskId);
        asyncTasks.put(taskId, state);

        applicationContext.getBean(ExerciseService.class).executeBulkSaveTask(taskId, requests);

        return new AsyncTaskCreateResponse(taskId);
    }

    public AsyncTaskStatusResponse getTaskStatus(String taskId) {
        AsyncTaskState state = asyncTasks.get(taskId);

        if (state == null)
            throw new NotFoundException("асинхронная задача с таким id не найдена");

        return state.toResponse(completedAsyncTasks.get());
    }

    @Async
    public void executeBulkSaveTask(
            String taskId, List<ExerciseRequest> requests) {
        AsyncTaskState state = asyncTasks.get(taskId);

        if (state == null)
            return;
        state.markRunning();
        
        try {
            List<ExerciseResponse> responses = saveAllWithoutTransactional(requests);

            state.markCompleted(responses.size());
            completedAsyncTasks.incrementAndGet();
        } catch (Exception e) {
            state.markFailed(e.getMessage());
        }
    }

}
