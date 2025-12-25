package com.kolmir.fitness_tracker.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;

    public static String getExceptionMessage(BindingResult bindingResult) {
        StringBuilder msgBuild = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        errors.stream().forEach(error -> msgBuild.append(error.getField() + " - " + error.getDefaultMessage()));
        return msgBuild.toString();
    }
}
