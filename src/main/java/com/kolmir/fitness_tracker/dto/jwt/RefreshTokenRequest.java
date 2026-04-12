package com.kolmir.fitness_tracker.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
@Schema(description = "Запрос на обновление access token")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    @NotNull
    private String refreshToken;
}
