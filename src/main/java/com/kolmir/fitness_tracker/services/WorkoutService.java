package com.kolmir.fitness_tracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.exceptions.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.mappers.WorkoutMapper;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.repository.WorkoutSpecifications;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final WorkoutMapper workoutMapper;

    @Transactional(readOnly = true)
    public Page<WorkoutDTO> getAllByOwnerId(WorkoutFilter workoutFilter, Pageable pageable) {
        Specification<Workout> specification = WorkoutSpecifications.withFilter(workoutFilter);
        return workoutRepository.findAll(specification, pageable).map(workoutMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public WorkoutDTO getById(Long id) throws WorkoutNotFoundException {
        return workoutMapper.toDTO(workoutRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("Тренеровка с таким id не найдена.")
        ));
    }

    @Transactional
    public WorkoutDTO save(WorkoutDTO workoutDTO) {
        workoutDTO.setOwnerId(CurrentUserProvider.getCurrentUserId());
        Workout workout = workoutMapper.toEntity(workoutDTO);
        return workoutMapper.toDTO(workoutRepository.save(workout));
    }

    @Transactional
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public WorkoutDTO update(Long id, WorkoutDTO workoutDTO) throws WorkoutNotFoundException {
        Workout workout = workoutMapper.toEntity(workoutDTO);
        workout.setId(id);

        if (!workoutRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно обновить несуществующую тренировку");

        return workoutMapper.toDTO(workoutRepository.save(workout));
    }

    @Transactional
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws WorkoutNotFoundException {
        if (!workoutRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно удалить несуществующую тренировку");
        workoutRepository.deleteById(id);
    }
}
