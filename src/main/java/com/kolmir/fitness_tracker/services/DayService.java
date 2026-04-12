package com.kolmir.fitness_tracker.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.day.DayCreateRequest;
import com.kolmir.fitness_tracker.dto.day.DayResponse;
import com.kolmir.fitness_tracker.dto.day.DayUpdateRequest;
import com.kolmir.fitness_tracker.exceptions.ConflictException;
import com.kolmir.fitness_tracker.exceptions.NotFoundException;
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
    private final CustomUserDetailsService userDetailsService;
    private final DayMapper dayMapper;

    @Transactional(readOnly = true)
    public List<DayResponse> getAllDays(Optional<String> workoutName) {
        Long userId = userDetailsService.getCurrentUserId();
        List<Day> days; 

        if (workoutName.isPresent())
            days = dayRepository.findAllByWorkoutNameAndOwnerId(workoutName.get(), userId);
        else
            days = dayRepository.findAllByOwnerId(userId);
        
        return days.stream()
                .map(dayMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DayResponse getByDate(LocalDate date) {
        Day day = getForUserByDate(date);
        return dayMapper.toResponse(day);
    }

    public DayResponse create(DayCreateRequest request) {
        if (dayRepository.findDayByDateAndOwnerId(request.getDate(), CurrentUserProvider.getCurrentUserId()).isPresent())
            throw new ConflictException("запись на эту дату уже создана");
        return dayMapper.toResponse(dayRepository.save(dayMapper.toDay(request)));
    }

    public DayResponse update(DayUpdateRequest request) {
        Day day = getForUserByDate(request.getDate());

        if (request.getCalories() != null) 
            day.setCalories(request.getCalories());
        if (request.getWorkoutId() != null) 
            day.setWorkout(workoutRepository.getReferenceById(request.getWorkoutId()));

        return dayMapper.toResponse(dayRepository.save(day));
    }

    public void delete(Long id) {
        Day day = dayRepository.findById(id).orElseThrow(
            () -> new NotFoundException("в этот день тренировки не было")
        );
        dayRepository.delete(day);
    }

    private Day getForUserByDate(LocalDate date) {
        Long ownerId = CurrentUserProvider.getCurrentUserId();

        Day day = dayRepository.findDayByDateAndOwnerId(date, ownerId).orElseThrow(
            () -> new NotFoundException("в этот день тренировки не было")
        );

        return day;
    }
}
