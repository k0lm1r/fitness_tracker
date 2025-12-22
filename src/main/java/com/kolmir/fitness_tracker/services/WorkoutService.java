package com.kolmir.fitness_tracker.services;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.repository.WorkoutSpecifications;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<Workout> getAllByOwnerId(WorkoutFilter workoutFilter, Pageable pageable) {
        Specification<Workout> specification = WorkoutSpecifications.withFilter(workoutFilter);
        return workoutRepository.findAll(specification, pageable);        
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public Workout getById(Long id) throws WorkoutNotFoundException {
        return workoutRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("Тренеровка с таким id не найдена.")
        );
    }

    @Transactional
    public Workout save(Workout workout) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workout.setOwner(user);
        return workoutRepository.save(workout);
    }

    @Transactional
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public Workout update(Long id, Workout workout) throws WorkoutNotFoundException {
        workout.setId(id);
        if (!workoutRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно обновить несуществующую тренировку");
        return workoutRepository.save(workout);
    }

    @Transactional
    @PreAuthorize("@workoutRepository.existsByIdAndOwnerId(#id, authentication.principal.id)")
    public void delete(Long id) throws WorkoutNotFoundException {
        if (!workoutRepository.existsById(id))
            throw new WorkoutNotFoundException("невозможно удалить несуществующую тренировку");
        workoutRepository.deleteById(id);
    }

    public Workout DTOtoEntity(WorkoutDTO workoutDTO) {
        Workout workout = modelMapper.map(workoutDTO, Workout.class);
        
        if (workoutDTO.getCategoryId() != null)
            workout.setCategory(categoryRepository.getReferenceById(workoutDTO.getCategoryId()));

        return workout;
    }

    public WorkoutDTO entityToDTO(Workout workout) {
        WorkoutDTO workoutDTO = modelMapper.map(workout, WorkoutDTO.class);
            
        if (workout.getCategory() != null)
            workoutDTO.setCategoryId(workout.getCategory().getId());

        return workoutDTO;
    }
}
