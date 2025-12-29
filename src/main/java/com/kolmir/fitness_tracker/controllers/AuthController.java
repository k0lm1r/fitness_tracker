package com.kolmir.fitness_tracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.controllers.api.AuthControllerApi;
import com.kolmir.fitness_tracker.dto.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.JwtNotValidException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @PostMapping("/login")
    @Override
    public ResponseEntity<?> signin(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Override
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) throws JwtNotValidException {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<?> signup(@Valid @RequestBody UserRegisterRequest request) throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        return ResponseEntity.ok(authService.register(request));
    }
}
