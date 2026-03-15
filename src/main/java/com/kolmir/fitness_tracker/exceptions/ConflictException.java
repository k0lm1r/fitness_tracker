package com.kolmir.fitness_tracker.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends FitnessTrackerException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
