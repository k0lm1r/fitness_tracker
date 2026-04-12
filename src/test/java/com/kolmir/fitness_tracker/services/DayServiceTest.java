package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.exceptions.ConflictException;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
import com.kolmir.fitness_tracker.mappers.DayMapper;
import com.kolmir.fitness_tracker.models.Day;
import com.kolmir.fitness_tracker.models.Workout;
import com.kolmir.fitness_tracker.repository.DayRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

@ExtendWith(MockitoExtension.class)
class DayServiceTest {

    @Mock
    private DayRepository dayRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private DayMapper dayMapper;

    @InjectMocks
    private DayService dayService;

    @Test
    void getAllDaysFiltersByWorkoutWhenNameProvided() {
        Day day = new Day();
        DayResponse response = new DayResponse();

        when(userDetailsService.getCurrentUserId()).thenReturn(3L);
        when(dayRepository.findAllByWorkoutNameAndOwnerId("Upper", 3L)).thenReturn(List.of(day));
        when(dayMapper.toResponse(day)).thenReturn(response);

        List<DayResponse> result = dayService.getAllDays(Optional.of("Upper"));

        assertEquals(1, result.size());
        assertEquals(response, result.getFirst());
    }

    @Test
    void getAllDaysReturnsAllForUserWhenWorkoutMissing() {
        Day day = new Day();
        DayResponse response = new DayResponse();

        when(userDetailsService.getCurrentUserId()).thenReturn(4L);
        when(dayRepository.findAllByOwnerId(4L)).thenReturn(List.of(day));
        when(dayMapper.toResponse(day)).thenReturn(response);

        List<DayResponse> result = dayService.getAllDays(Optional.empty());

        assertEquals(1, result.size());
        verify(dayRepository).findAllByOwnerId(4L);
    }

    @Test
    void getByDateThrowsWhenDayNotFound() {
        LocalDate date = LocalDate.of(2026, 3, 1);

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(1L);
            when(dayRepository.findDayByDateAndOwnerId(date, 1L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> dayService.getByDate(date));
        }
    }

    @Test
    void getByDateReturnsMappedDayWhenFound() {
        LocalDate date = LocalDate.of(2026, 3, 1);
        Day day = new Day();
        DayResponse response = new DayResponse();

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(2L);
            when(dayRepository.findDayByDateAndOwnerId(date, 2L)).thenReturn(Optional.of(day));
            when(dayMapper.toResponse(day)).thenReturn(response);

            DayResponse result = dayService.getByDate(date);

            assertEquals(response, result);
        }
    }

    @Test
    void createThrowsConflictWhenDayAlreadyExists() {
        DayCreateRequest request = new DayCreateRequest();
        request.setDate(LocalDate.of(2026, 3, 2));

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(5L);
            when(dayRepository.findDayByDateAndOwnerId(request.getDate(), 5L)).thenReturn(Optional.of(new Day()));

            assertThrows(ConflictException.class, () -> dayService.create(request));
        }
    }

    @Test
    void createPersistsNewDayWhenDateIsFree() {
        DayCreateRequest request = new DayCreateRequest();
        request.setDate(LocalDate.of(2026, 3, 3));

        Day mapped = new Day();
        DayResponse response = new DayResponse();

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(6L);
            when(dayRepository.findDayByDateAndOwnerId(request.getDate(), 6L)).thenReturn(Optional.empty());
            when(dayMapper.toDay(request)).thenReturn(mapped);
            when(dayRepository.save(mapped)).thenReturn(mapped);
            when(dayMapper.toResponse(mapped)).thenReturn(response);

            DayResponse result = dayService.create(request);

            assertEquals(response, result);
        }
    }

    @Test
    void updateChangesCaloriesAndWorkout() {
        LocalDate date = LocalDate.of(2026, 3, 4);
        DayUpdateRequest request = new DayUpdateRequest();
        request.setDate(date);
        request.setCalories(700);
        request.setWorkoutId(42L);

        Day existing = new Day();
        existing.setDate(date);
        existing.setCalories(400);

        Workout workout = new Workout();
        DayResponse response = new DayResponse();

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(8L);
            when(dayRepository.findDayByDateAndOwnerId(date, 8L)).thenReturn(Optional.of(existing));
            when(workoutRepository.getReferenceById(42L)).thenReturn(workout);
            when(dayRepository.save(existing)).thenReturn(existing);
            when(dayMapper.toResponse(existing)).thenReturn(response);

            DayResponse result = dayService.update(request);

            assertEquals(700, existing.getCalories());
            assertEquals(workout, existing.getWorkout());
            assertEquals(response, result);
        }
    }

    @Test
    void updateSkipsNullableFields() {
        LocalDate date = LocalDate.of(2026, 3, 5);
        DayUpdateRequest request = new DayUpdateRequest();
        request.setDate(date);

        Day existing = new Day();
        existing.setDate(date);
        existing.setCalories(300);
        DayResponse response = new DayResponse();

        try (MockedStatic<CurrentUserProvider> currentUser = mockStatic(CurrentUserProvider.class)) {
            currentUser.when(CurrentUserProvider::getCurrentUserId).thenReturn(9L);
            when(dayRepository.findDayByDateAndOwnerId(date, 9L)).thenReturn(Optional.of(existing));
            when(dayRepository.save(existing)).thenReturn(existing);
            when(dayMapper.toResponse(existing)).thenReturn(response);

            DayResponse result = dayService.update(request);

            assertEquals(300, existing.getCalories());
            assertEquals(response, result);
            verify(workoutRepository, never()).getReferenceById(any());
        }
    }

    @Test
    void deleteThrowsWhenDayMissing() {
        when(dayRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dayService.delete(55L));
        verify(dayRepository, never()).delete(any(Day.class));
    }

    @Test
    void deleteRemovesFoundDay() {
        Day day = new Day();
        when(dayRepository.findById(56L)).thenReturn(Optional.of(day));

        dayService.delete(56L);

        verify(dayRepository).delete(day);
    }
}
