package com.kolmir.fitness_tracker.utils;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kolmir.fitness_tracker.utils.jwt.JwtNotValidException;
import com.kolmir.fitness_tracker.utils.user.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.utils.user.UserNotValidException;
import com.kolmir.fitness_tracker.utils.user.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotFoundException;
import com.kolmir.fitness_tracker.utils.workout.WorkoutNotValidException;

@RestControllerAdvice
public class FitnessTrackerExceptionHandler {
    @ExceptionHandler(WorkoutNotFoundException.class)
    ResponseEntity<ErrorResponse> handleException(WorkoutNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkoutNotValidException.class)
    ResponseEntity<ErrorResponse> handleException(WorkoutNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {
        ErrorResponse errorResponse = new ErrorResponse("Неверный логин или пароль", LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("Неверный логин или пароль", LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameAlreadyExistsException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<?> handleUsernameNotFound(EmailAlreadyInUseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotValidException.class)
    public ResponseEntity<?> handleUsernameNotFound(UserNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtNotValidException.class)
    public ResponseEntity<?> handleUsernameNotFound(JwtNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
