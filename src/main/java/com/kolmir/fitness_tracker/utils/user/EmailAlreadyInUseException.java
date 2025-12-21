package com.kolmir.fitness_tracker.utils.user;

public class EmailAlreadyInUseException extends Exception {
    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
