package com.kolmir.fitness_tracker.exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.validation.BeanPropertyBindingResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public static String getExceptionMessage(BeanPropertyBindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
