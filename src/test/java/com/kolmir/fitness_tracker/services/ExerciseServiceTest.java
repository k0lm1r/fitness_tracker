package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import com.kolmir.fitness_tracker.cache.ExerciseCache;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskCreateResponse;
import com.kolmir.fitness_tracker.dto.exercise.AsyncTaskStatusResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.repository.ExerciseRepository;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private ExerciseCache cache;

    @InjectMocks
    private ExerciseService exerciseService;

    @Test
    void getAllByOwnerIdReturnsCachedPageWhenCacheHit() {
        ExerciseFilter filter = new ExerciseFilter();
        var pageable = PageRequest.of(0, 10);
        Page<ExerciseResponse> cachedPage = new PageImpl<>(List.of(new ExerciseResponse()));

        when(cache.containsKey(any())).thenReturn(true);
        when(cache.get(any())).thenReturn(cachedPage);

        Page<ExerciseResponse> result = exerciseService.getAllByOwnerId(filter, pageable);

        assertEquals(cachedPage, result);
        verify(exerciseRepository, never()).findAll(org.mockito.ArgumentMatchers.<Specification<Exercise>>any(), eq(pageable));
    }

    @Test
    void getAllByOwnerIdLoadsFromRepositoryAndCachesWhenCacheMiss() {
        ExerciseFilter filter = new ExerciseFilter();
        var pageable = PageRequest.of(0, 10);
        Exercise exercise = new Exercise();
        ExerciseResponse response = new ExerciseResponse();
        response.setId(1L);

        Page<Exercise> exercisePage = new PageImpl<>(List.of(exercise));

        when(cache.containsKey(any())).thenReturn(false);
        when(exerciseRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Exercise>>any(), eq(pageable)))
                .thenReturn(exercisePage);
        when(exerciseMapper.toResponse(exercise)).thenReturn(response);

        Page<ExerciseResponse> result = exerciseService.getAllByOwnerId(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().getId());
        verify(cache).put(any(), any());
    }

    @Test
    void getByIdThrowsWhenExerciseNotFound() {
        when(exerciseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> exerciseService.getById(99L));
    }

    @Test
    void getByIdReturnsMappedExerciseWhenFound() {
        Exercise exercise = new Exercise();
        ExerciseResponse response = new ExerciseResponse();
        response.setId(101L);

        when(exerciseRepository.findById(101L)).thenReturn(Optional.of(exercise));
        when(exerciseMapper.toResponse(exercise)).thenReturn(response);

        ExerciseResponse result = exerciseService.getById(101L);

        assertEquals(101L, result.getId());
    }

    @Test
    void saveMapsAndPersistsExercise() {
        ExerciseRequest request = new ExerciseRequest();
        Exercise exercise = new Exercise();
        Exercise saved = new Exercise();
        saved.setId(10L);
        ExerciseResponse response = new ExerciseResponse();
        response.setId(10L);

        when(exerciseMapper.toEntity(request)).thenReturn(exercise);
        when(exerciseRepository.save(exercise)).thenReturn(saved);
        when(exerciseMapper.toResponse(saved)).thenReturn(response);

        ExerciseResponse result = exerciseService.save(request);

        assertEquals(10L, result.getId());
        verify(cache).invalidate();
    }

    @Test
    void updateThrowsWhenExerciseMissing() {
        ExerciseRequest request = new ExerciseRequest();
        Exercise exercise = new Exercise();

        when(exerciseMapper.toEntity(request)).thenReturn(exercise);
        when(exerciseRepository.existsById(500L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> exerciseService.update(500L, request));

        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    void updateSavesExerciseWhenExists() {
        ExerciseRequest request = new ExerciseRequest();
        Exercise exercise = new Exercise();
        ExerciseResponse response = new ExerciseResponse();
        response.setId(501L);

        when(exerciseMapper.toEntity(request)).thenReturn(exercise);
        when(exerciseRepository.existsById(501L)).thenReturn(true);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseMapper.toResponse(exercise)).thenReturn(response);

        ExerciseResponse result = exerciseService.update(501L, request);

        assertEquals(501L, exercise.getId());
        assertEquals(501L, result.getId());
        verify(cache).invalidate();
    }

    @Test
    void deleteThrowsWhenExerciseMissing() {
        when(exerciseRepository.existsById(700L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> exerciseService.delete(700L));

        verify(exerciseRepository, never()).deleteById(any());
    }

    @Test
    void deleteRemovesExerciseWhenExists() {
        when(exerciseRepository.existsById(701L)).thenReturn(true);

        exerciseService.delete(701L);

        verify(cache).invalidate();
        verify(exerciseRepository).deleteById(701L);
    }

    @Test
    void saveAllWithoutTransactionalPersistsMappedExercises() {
        ExerciseRequest request1 = new ExerciseRequest();
        ExerciseRequest request2 = new ExerciseRequest();
        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();
        ExerciseResponse response1 = new ExerciseResponse();
        ExerciseResponse response2 = new ExerciseResponse();

        when(exerciseMapper.toEntity(request1)).thenReturn(exercise1);
        when(exerciseMapper.toEntity(request2)).thenReturn(exercise2);
        when(exerciseRepository.save(exercise1)).thenReturn(exercise1);
        when(exerciseRepository.save(exercise2)).thenReturn(exercise2);
        when(exerciseMapper.toResponse(exercise1)).thenReturn(response1);
        when(exerciseMapper.toResponse(exercise2)).thenReturn(response2);

        List<ExerciseResponse> result = exerciseService.saveAllWithoutTransactional(List.of(request1, request2));

        assertEquals(2, result.size());
        verify(exerciseRepository).save(exercise1);
        verify(exerciseRepository).save(exercise2);
    }

    @Test
    void saveAllWithTransactionalReturnsSameBulkResult() {
        ExerciseRequest request = new ExerciseRequest();
        Exercise exercise = new Exercise();
        ExerciseResponse response = new ExerciseResponse();

        when(exerciseMapper.toEntity(request)).thenReturn(exercise);
        when(exerciseRepository.banchSave(List.of(exercise))).thenReturn(List.of(exercise));
        when(exerciseMapper.toResponse(exercise)).thenReturn(response);

        List<ExerciseResponse> result = exerciseService.saveAllWithTransactional(List.of(request));

        assertEquals(1, result.size());
        verify(exerciseRepository).banchSave(List.of(exercise));
    }

    @Test
    void startBulkSaveTaskReturnsTaskIdAndCreatesTrackableTask() {
        List<ExerciseRequest> requests = List.of(new ExerciseRequest());

        AsyncTaskCreateResponse result = exerciseService.startBulkSaveTask(requests);
        AsyncTaskStatusResponse status = exerciseService.getTaskStatus(result.getTaskId());

        org.junit.jupiter.api.Assertions.assertNotNull(result.getTaskId());
        org.junit.jupiter.api.Assertions.assertNotNull(status.getStatus());
    }

    @Test
    void getTaskStatusThrowsWhenTaskMissing() {
        assertThrows(NotFoundException.class, () -> exerciseService.getTaskStatus("missing-task-id"));
    }

    @Test
    void executeBulkSaveTaskCompletesAndUpdatesStatus() {
        ExerciseRequest request = new ExerciseRequest();
        Exercise exercise = new Exercise();
        ExerciseResponse response = new ExerciseResponse();
        response.setId(1L);

        when(exerciseMapper.toEntity(request)).thenReturn(exercise);
        when(exerciseRepository.banchSave(List.of(exercise))).thenReturn(List.of(exercise));
        when(exerciseMapper.toResponse(exercise)).thenReturn(response);
        AsyncTaskCreateResponse created = exerciseService.startBulkSaveTask(List.of(request));
        AsyncTaskStatusResponse status = exerciseService.getTaskStatus(created.getTaskId());
        for (int i = 0; i < 20 && !"COMPLETED".equals(status.getStatus()); i++) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            status = exerciseService.getTaskStatus(created.getTaskId());
        }

        assertEquals("COMPLETED", status.getStatus());
        assertEquals(1, status.getProcessedCount());
        assertEquals(1, status.getCompletedTasksTotal());
    }

}
