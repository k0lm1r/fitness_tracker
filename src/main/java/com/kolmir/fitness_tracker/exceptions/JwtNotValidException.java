package com.kolmir.fitness_tracker.exceptions;

public class JwtNotValidException extends Exception {
    public JwtNotValidException(String message) {
        super(message);
    }   
}
