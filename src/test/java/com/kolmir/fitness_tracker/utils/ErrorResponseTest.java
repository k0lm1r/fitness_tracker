package com.kolmir.fitness_tracker.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

class ErrorResponseTest {

    @Test
    void getExceptionMessage_ShouldConcatenateFieldErrors() {
        record Sample(String name, int age) {}
        Sample sample = new Sample("", 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(sample, "sample");
        bindingResult.addError(new FieldError("sample", "name", "must not be blank"));
        bindingResult.addError(new FieldError("sample", "age", "must be greater than 0"));

        String message = ErrorResponse.getExceptionMessage(bindingResult);

        assertTrue(message.contains("name - must not be blank"));
        assertTrue(message.contains("age - must be greater than 0"));
    }

    @Test
    void errorResponse_HoldsMessageAndTimestamp() {
        ErrorResponse response = new ErrorResponse("oops", java.time.LocalDateTime.now());
        assertTrue(response.getMessage().contains("oops"));
        assertTrue(response.getTimestamp() != null);
    }
}
