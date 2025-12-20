package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.kolmir.fitness_tracker.dto.WorkoutDTO;
import com.kolmir.fitness_tracker.dto.WorkoutFilter;
import com.kolmir.fitness_tracker.models.Category;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.CategoryRepository;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void getById_WhenWorkoutExists_ReturnsWorkout() throws WorkoutNotFoundException {
        Workout workout = new Workout();
        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        Workout result = workoutService.getById(1L);

        assertSame(workout, result);
        verify(workoutRepository).findById(1L);
    }

    @Test
    void getById_WhenWorkoutMissing_ThrowsException() {
        when(workoutRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.getById(5L));
        verify(workoutRepository).findById(5L);
    }

    @Test
    void delete_WhenWorkoutExists_DeletesIt() throws WorkoutNotFoundException {
        when(workoutRepository.findById(3L)).thenReturn(Optional.of(new Workout()));

        workoutService.delete(3L);

        verify(workoutRepository).deleteById(3L);
    }

    @Test
    void delete_WhenWorkoutMissing_ThrowsException() {
        when(workoutRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.delete(9L));
        verify(workoutRepository).findById(9L);
    }

    @Test
    void getAllByOwnerId_ReturnsPageFromRepository() {
        WorkoutFilter filter = new WorkoutFilter();
        filter.setOwnerId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        List<Workout> workouts = List.of(new Workout(), new Workout());
        Page<Workout> page = new PageImpl<>(workouts, pageable, workouts.size());

        Page<Workout> result = workoutService.getAllByOwnerId(filter, pageable);

        assertSame(page, result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void update_ShouldSetIdAndSave() {
        Workout workout = new Workout();

        workoutService.update(5L, workout);

        assertEquals(5L, workout.getId());
        verify(workoutRepository).save(workout);
    }

    @Test
    void DTOtoEntity_ShouldMapFieldsAndRelations() {
        WorkoutDTO dto = new WorkoutDTO();
        dto.setName("Cardio");
        dto.setWorkoutDate(LocalDateTime.now());
        dto.setDurationMinutes(30);
        dto.setCalories(400);
        dto.setCategoryId(2L);
        dto.setOwnerId(3L);

        Workout mappedWorkout = new Workout();
        mappedWorkout.setName(dto.getName());
        mappedWorkout.setWorkoutDate(dto.getWorkoutDate());
        mappedWorkout.setDurationMinutes(dto.getDurationMinutes());
        mappedWorkout.setCalories(dto.getCalories());

        Category category = new Category();
        category.setId(2L);
        User owner = new User();
        owner.setId(3L);

        when(modelMapper.map(dto, Workout.class)).thenReturn(mappedWorkout);
        when(categoryRepository.getReferenceById(2L)).thenReturn(category);
        when(userRepository.getReferenceById(3L)).thenReturn(owner);

        Workout result = workoutService.DTOtoEntity(dto);

        assertSame(category, result.getCategory());
        assertSame(owner, result.getOwner());
        assertEquals("Cardio", result.getName());
    }

    @Test
    void entityToDTO_ShouldMapAndAddIds() {
        Workout workout = new Workout();
        workout.setName("Strength");
        workout.setWorkoutDate(LocalDateTime.now());
        workout.setDurationMinutes(50);
        workout.setCalories(550);

        Category category = new Category();
        category.setId(10L);
        User owner = new User();
        owner.setId(11L);
        workout.setCategory(category);
        workout.setOwner(owner);

        WorkoutDTO mappedDto = new WorkoutDTO();
        mappedDto.setName(workout.getName());
        mappedDto.setWorkoutDate(workout.getWorkoutDate());
        mappedDto.setDurationMinutes(workout.getDurationMinutes());
        mappedDto.setCalories(workout.getCalories());

        when(modelMapper.map(workout, WorkoutDTO.class)).thenReturn(mappedDto);

        WorkoutDTO result = workoutService.entityToDTO(workout);

        assertEquals(10L, result.getCategoryId());
        assertEquals(11L, result.getOwnerId());
        assertEquals(550, result.getCalories());
        verify(modelMapper).map(workout, WorkoutDTO.class);
    }
}
