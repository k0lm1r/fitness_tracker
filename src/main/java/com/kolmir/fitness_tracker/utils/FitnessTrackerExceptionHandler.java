package com.kolmir.fitness_tracker.utils;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotValidException;

@RestControllerAdvice
public class FitnessTrackerExceptionHandler {
    @ExceptionHandler(WorkoutNotFoundException.class)
    ResponseEntity<ErrorResponse> handleException(WorkoutNotFoundException e) {
        ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponce, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkoutNotValidException.class)
    ResponseEntity<ErrorResponse> handleException(WorkoutNotValidException e) {
        ErrorResponse errorResponce = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponce, HttpStatus.BAD_REQUEST);
    }
}
