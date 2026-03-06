package com.kolmir.fitness_tracker.dto.jwt;

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
