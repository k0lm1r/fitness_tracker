package com.kolmir.fitness_tracker.exceptions;

import org.springframework.http.HttpStatus;

public class StorageException extends FitnessTrackerException {
    public StorageException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}
