package com.kolmir.fitness_tracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class RefreshTokenRequest {
    @NotNull
    private String refreshToken;
}
