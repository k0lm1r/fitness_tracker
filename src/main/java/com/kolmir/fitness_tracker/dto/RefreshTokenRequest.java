package com.kolmir.fitness_tracker.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class RefreshTokenRequest {
    private String refreshToken;
}
