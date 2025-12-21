package com.kolmir.fitness_tracker.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kolmir.fitness_tracker.dto.JwtResponse;
import com.kolmir.fitness_tracker.dto.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.security.JwtUtils;
import com.kolmir.fitness_tracker.services.UserService;
import com.kolmir.fitness_tracker.utils.jwt.JwtNotValidException;
import com.kolmir.fitness_tracker.utils.user.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.utils.user.UsernameAlreadyExistsException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> signin(@RequestBody UserLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) throws JwtNotValidException {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new JwtNotValidException("не валидный токен");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtils.generateAccessToken(username);

        return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRegisterRequest request) throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        userService.createUser(modelMapper.map(request, User.class));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }
}