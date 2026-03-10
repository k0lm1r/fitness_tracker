package com.kolmir.fitness_tracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseResponse;
import com.kolmir.fitness_tracker.dto.exercise.ExerciseFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
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

    @Transactional(readOnly = true)
    public Page<ExerciseResponse> getAllByOwnerId(ExerciseFilter exerciseFilter, Pageable pageable) {
        Specification<Exercise> specification = ExerciseSpecifications.withFilter(exerciseFilter);
        return exerciseRepository.findAll(specification, pageable).map(exerciseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseResponse getById(Long id) throws WorkoutNotFoundException {
        return exerciseMapper.toResponse(exerciseRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("Упражнение с таким id не найдено.")
        ));
    }

    @Transactional
    public ExerciseResponse save(ExerciseRequest exerciseRequest) {
        Exercise exercise = exerciseMapper.toEntity(exerciseRequest);
        return exerciseMapper.toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseResponse update(Long id, ExerciseRequest exerciseRequest) throws WorkoutNotFoundException {
        Exercise exercise = exerciseMapper.toEntity(exerciseRequest);
        exercise.setId(id);

        if (!exerciseRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно обновить несуществующее упражнение");

        return exerciseMapper.toResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws WorkoutNotFoundException {
        if (!exerciseRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно удалить несуществующее упражнение");
        exerciseRepository.deleteById(id);
    }
}
