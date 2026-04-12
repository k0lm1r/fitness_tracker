package com.kolmir.fitness_tracker.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends FitnessTrackerException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
