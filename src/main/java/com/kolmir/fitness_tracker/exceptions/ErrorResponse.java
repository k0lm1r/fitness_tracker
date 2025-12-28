package com.kolmir.fitness_tracker.exceptions;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}
