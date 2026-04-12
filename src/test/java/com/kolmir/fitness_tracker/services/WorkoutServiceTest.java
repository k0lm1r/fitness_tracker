package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.fitness_tracker.dto.exercise.ExerciseRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutRequest;
import com.kolmir.fitness_tracker.dto.workout.WorkoutResponse;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.ExerciseMapper;
import com.kolmir.fitness_tracker.mappers.WorkoutMapper;
import com.kolmir.fitness_tracker.models.Exercise;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private WorkoutMapper workoutMapper;

    @Mock
    private ExerciseMapper exerciseMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private WorkoutService workoutService;

    @Test
    void saveWithoutTransactionalUpdatesExercisesAndInvalidatesCache() {
        WorkoutRequest request = new WorkoutRequest();
        WorkoutResponse response = new WorkoutResponse();
        ExerciseRequest exerciseRequest = new ExerciseRequest();

        Exercise exercise = new Exercise();
        exercise.setId(1L);
        exercise.setWorkouts(new HashSet<>());

        Set<Exercise> exercises = new HashSet<>();
        exercises.add(exercise);

        Workout workout = new Workout();
        workout.setExercises(exercises);
        User currentUser = new User();
        currentUser.setId(5L);

        when(workoutMapper.toWorkout(request)).thenReturn(workout);
        when(userDetailsService.getCurrentUserId()).thenReturn(5L);
        when(userRepository.getReferenceById(5L)).thenReturn(currentUser);
        when(workoutRepository.save(any(Workout.class))).thenAnswer(invocation -> {
            Workout argument = invocation.getArgument(0);
            if (argument.getId() == null) {
                argument.setId(20L);
            }
            return argument;
        });
        when(exerciseMapper.toRequest(exercise)).thenReturn(exerciseRequest);
        when(workoutMapper.toWorkoutResponse(any(Workout.class))).thenReturn(response);

        WorkoutResponse result = workoutService.saveWithoutTransactional(request);

        assertEquals(response, result);
        verify(exerciseService).update(1L, exerciseRequest);
        verify(exerciseService).invalidateCache();
        verify(workoutRepository, times(2)).save(any(Workout.class));
    }

    @Test
    void getByIdThrowsWhenWorkoutMissing() {
        when(workoutRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> workoutService.getById(77L));
    }

    @Test
    void getByIdReturnsMappedWorkout() {
        Workout workout = new Workout();
        WorkoutResponse response = new WorkoutResponse();

        when(workoutRepository.findById(78L)).thenReturn(Optional.of(workout));
        when(workoutMapper.toWorkoutResponse(workout)).thenReturn(response);

        WorkoutResponse result = workoutService.getById(78L);

        assertEquals(response, result);
    }

    @Test
    void updateThrowsWhenWorkoutMissing() {
        WorkoutRequest request = new WorkoutRequest();
        when(workoutRepository.existsById(88L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> workoutService.update(88L, request));
        verify(workoutRepository, never()).save(any(Workout.class));
    }

    @Test
    void updatePersistsWorkoutWhenExists() {
        WorkoutRequest request = new WorkoutRequest();
        Workout workout = new Workout();
        WorkoutResponse response = new WorkoutResponse();
        User currentUser = new User();
        currentUser.setId(9L);

        when(workoutRepository.existsById(89L)).thenReturn(true);
        when(workoutMapper.toWorkout(request)).thenReturn(workout);
        when(userDetailsService.getCurrentUserId()).thenReturn(9L);
        when(userRepository.getReferenceById(9L)).thenReturn(currentUser);
        when(workoutRepository.save(workout)).thenReturn(workout);
        when(workoutMapper.toWorkoutResponse(workout)).thenReturn(response);

        WorkoutResponse result = workoutService.update(89L, request);

        assertEquals(89L, workout.getId());
        assertEquals(response, result);
    }

    @Test
    void deleteExistingWorkoutInvalidatesCacheAndDeletes() {
        when(workoutRepository.existsById(99L)).thenReturn(true);

        workoutService.delete(99L);

        verify(exerciseService).invalidateCache();
        verify(workoutRepository).deleteById(99L);
    }

    @Test
    void deleteThrowsWhenWorkoutMissing() {
        when(workoutRepository.existsById(100L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> workoutService.delete(100L));
        verify(workoutRepository, never()).deleteById(100L);
    }

    @Test
    void getAllReturnsMappedResponses() {
        Workout workout = new Workout();
        WorkoutResponse response = new WorkoutResponse();

        when(workoutRepository.findAll()).thenReturn(List.of(workout));
        when(workoutMapper.toWorkoutResponse(workout)).thenReturn(response);

        List<WorkoutResponse> result = workoutService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
    }

    @Test
    void saveWithTransactionalDelegatesToSaveWithoutTransactional() {
        WorkoutRequest request = new WorkoutRequest();
        WorkoutResponse response = new WorkoutResponse();

        Workout workout = new Workout();
        workout.setExercises(new HashSet<>());
        User currentUser = new User();
        currentUser.setId(10L);

        when(workoutMapper.toWorkout(request)).thenReturn(workout);
        when(userDetailsService.getCurrentUserId()).thenReturn(10L);
        when(userRepository.getReferenceById(10L)).thenReturn(currentUser);
        when(workoutRepository.save(any(Workout.class))).thenReturn(workout);
        when(workoutMapper.toWorkoutResponse(workout)).thenReturn(response);

        WorkoutResponse result = workoutService.saveWithTransactional(request);

        assertEquals(response, result);
        verify(workoutRepository, times(2)).save(any(Workout.class));
    }
}
