package com.kolmir.fitness_tracker.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.exceptions.DayAlreadyExistsException;
import com.kolmir.fitness_tracker.exceptions.DayNotFoundException;
import com.kolmir.fitness_tracker.mappers.DayMapper;
import com.kolmir.fitness_tracker.models.Day;
import com.kolmir.fitness_tracker.repository.DayRepository;
import com.kolmir.fitness_tracker.repository.WorkoutRepository;
import com.kolmir.fitness_tracker.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DayService {
    private final DayRepository dayRepository;
    private final WorkoutRepository workoutRepository;
    private final DayMapper dayMapper;

    @Transactional(readOnly = true)
    public List<DayResponse> getAllDays() {
        return dayRepository.findAll().stream()
                .map(dayMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DayResponse getByDate(LocalDate date) throws DayNotFoundException {
        Day day = getForUserByDate(date);
        return dayMapper.toResponse(day);
    }

    public DayResponse create(DayCreateRequest request) throws DayAlreadyExistsException {
        if (dayRepository.findDayByDateAndOwnerId(request.getDate(), CurrentUserProvider.getCurrentUserId()).isPresent())
            throw new DayAlreadyExistsException("запись на эту дату уже создана");
        return dayMapper.toResponse(dayRepository.save(dayMapper.toDay(request)));
    }

    public DayResponse update(DayUpdateRequest request) throws DayNotFoundException {
        Day day = getForUserByDate(request.getDate());

        if (request.getCalories() != null) 
            day.setCalories(request.getCalories());
        if (request.getWorkoutId() != null) 
            day.setWorkout(workoutRepository.getReferenceById(request.getWorkoutId()));

        return dayMapper.toResponse(dayRepository.save(day));
    }

    public void delete(Long id) throws DayNotFoundException {
        Day day = dayRepository.findById(id).orElseThrow(
            () -> new DayNotFoundException("в этот день тренировки не было")
        );
        dayRepository.delete(day);
    }

    private Day getForUserByDate(LocalDate date) throws DayNotFoundException {
        Long ownerId = CurrentUserProvider.getCurrentUserId();

        Day day = dayRepository.findDayByDateAndOwnerId(date, ownerId).orElseThrow(
            () -> new DayNotFoundException("в этот день тренировки не было")
        );

        return day;
    }
}
