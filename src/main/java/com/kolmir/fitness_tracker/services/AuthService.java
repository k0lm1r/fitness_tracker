package com.kolmir.fitness_tracker.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.kolmir.fitness_tracker.dto.JwtResponse;
import com.kolmir.fitness_tracker.dto.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.UserMapper;
import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.JwtNotValidException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;

    public JwtResponse login(UserLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refreshToken(RefreshTokenRequest request) throws JwtNotValidException {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new JwtNotValidException("невалидный токен");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtils.generateAccessToken(username);

        return new JwtResponse(newAccessToken, refreshToken);
    }
    
    public JwtResponse register(UserRegisterRequest request) throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        userService.createUser(userMapper.toEntity(request));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return new JwtResponse(accessToken, refreshToken);
    }
}
