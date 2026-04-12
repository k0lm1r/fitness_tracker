package com.kolmir.fitness_tracker.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class FitnessTrackerException extends RuntimeException {
    private final HttpStatus status;

    public FitnessTrackerException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
