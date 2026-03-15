package com.kolmir.fitness_tracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.cache.ExerciseCacheKey;
import com.kolmir.fitness_tracker.cache.ExerciseCache;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.repository.ExerciseRepository;
import com.kolmir.fitness_tracker.repository.ExerciseSpecifications;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseCache cache;

    @Transactional(readOnly = true)
    public Page<ExerciseResponse> getAllByOwnerId(ExerciseFilter exerciseFilter, Pageable pageable) {
        ExerciseCacheKey key = new ExerciseCacheKey("getByOwnerId", exerciseFilter);

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
}
