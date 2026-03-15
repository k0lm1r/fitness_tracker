package com.kolmir.fitness_tracker.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Schema(description = "Ответ с JWT токенами")
public class JwtResponse {
    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiJ9.access")
    private String access;

    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    private String refresh;
}
