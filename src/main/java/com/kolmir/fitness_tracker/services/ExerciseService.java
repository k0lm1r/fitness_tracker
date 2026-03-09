package com.kolmir.fitness_tracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseDTO;
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
    private final ExerciseMapper workoutMapper;

    @Transactional(readOnly = true)
    public Page<ExerciseDTO> getAllByOwnerId(ExerciseFilter workoutFilter, Pageable pageable) {
        Specification<Exercise> specification = ExerciseSpecifications.withFilter(workoutFilter);
        return exerciseRepository.findAll(specification, pageable).map(workoutMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseDTO getById(Long id) throws WorkoutNotFoundException {
        return workoutMapper.toDTO(exerciseRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("Тренеровка с таким id не найдена.")
        ));
    }

    @Transactional
    public ExerciseDTO save(ExerciseDTO workoutDTO) {
        Exercise workout = workoutMapper.toEntity(workoutDTO);
        return workoutMapper.toDTO(exerciseRepository.save(workout));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public ExerciseDTO update(Long id, ExerciseDTO workoutDTO) throws WorkoutNotFoundException {
        Exercise workout = workoutMapper.toEntity(workoutDTO);
        workout.setId(id);

        if (!exerciseRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно обновить несуществующую тренировку");

        return workoutMapper.toDTO(exerciseRepository.save(workout));
    }

    @Transactional
    @PreAuthorize("@exerciseRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws WorkoutNotFoundException {
        if (!exerciseRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно удалить несуществующую тренировку");
        exerciseRepository.deleteById(id);
    }
}
