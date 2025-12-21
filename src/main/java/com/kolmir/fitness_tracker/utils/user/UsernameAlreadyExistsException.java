package com.kolmir.fitness_tracker.utils.user;

public class UsernameAlreadyExistsException extends Exception {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
