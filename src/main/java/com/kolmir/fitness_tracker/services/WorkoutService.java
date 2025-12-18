package com.kolmir.fitness_tracker.services;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Workout> getAllByOwnerId() {
        //TODO Test realisation
        return workoutRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Workout getById(Long id) throws WorkoutNotFoundException {
        return workoutRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("Тренеровка с таким id не найдена.")
        );
    }

    @Transactional(readOnly = true)
    public List<Workout> filterByCategoryName(String name) {
        return workoutRepository.findByCategoryName(name);
    }

    @Transactional(readOnly = true)
    public List<Workout> filterByDuration(Integer min, Integer max) {
        return workoutRepository.findByDurationMinutesBetween(min, max);
    }

    @Transactional(readOnly = true) 
    public List<Workout> filterByOrder(boolean isDesc) {
        return isDesc ? workoutRepository.findAllByOrderByWorkoutDateDesc() :
                workoutRepository.findAllByOrderByCaloriesAsc();
    }

    @Transactional(readOnly = true)
    public List<Workout> filterByCalories(boolean isDesc) {
        return isDesc ? workoutRepository.findAllByOrderByCaloriesDesc() :
                workoutRepository.findAllByOrderByCaloriesAsc();
    }

    @Transactional(readOnly = true)
    public List<Workout> filterByWorkoutDate(LocalDateTime start, LocalDateTime end) {
        return workoutRepository.findAllByWorkoutDate(start, end);
    }

    @Transactional
    public Workout save(Workout workout) {
        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout update(Long id, Workout workout) {
        workout.setId(id);
        return workoutRepository.save(workout);
    }

    @Transactional
    public void delete(Long id) throws WorkoutNotFoundException {
        workoutRepository.findById(id).orElseThrow(
            () -> new WorkoutNotFoundException("невозможно удалить несуществующую тренировку")
        );
        workoutRepository.deleteById(id);
    }

    public Workout DTOtoEntity(WorkoutDTO workoutDTO) {
        Workout workout = modelMapper.map(workoutDTO, Workout.class);
        
        workout.setCategory(categoryRepository.getReferenceById(workoutDTO.getCategoryId()));
        workout.setOwner(userRepository.getReferenceById(workoutDTO.getOwnerId()));

        return workout;
    }

    public WorkoutDTO entityToDTO(Workout workout) {
        WorkoutDTO workoutDTO = modelMapper.map(workout, WorkoutDTO.class);
        workoutDTO.setCategoryId(workout.getCategory().getId());
        workoutDTO.setOwnerId(workout.getOwner().getId());

        return workoutDTO;
    }
}
