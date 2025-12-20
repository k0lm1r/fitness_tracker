package com.kolmir.fitness_tracker.utils.jwt;

public class JwtNotValidException extends Exception {
    public JwtNotValidException(String message) {
        super(message);
    }   
}
