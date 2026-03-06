package com.kolmir.fitness_tracker.dto.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class JwtResponse {
    private String access;
    private String refresh;
}
