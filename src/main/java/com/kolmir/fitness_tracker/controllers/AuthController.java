package com.kolmir.fitness_tracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.controllers.api.AuthApi;
import com.kolmir.fitness_tracker.dto.jwt.JwtResponse;
import com.kolmir.fitness_tracker.dto.jwt.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.user.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.user.UserRegisterRequest;
import com.kolmir.fitness_tracker.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> signin(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> signup(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
