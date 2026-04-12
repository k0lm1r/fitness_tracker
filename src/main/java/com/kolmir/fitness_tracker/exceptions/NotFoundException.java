package com.kolmir.fitness_tracker.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends FitnessTrackerException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
