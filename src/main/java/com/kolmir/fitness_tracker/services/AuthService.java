package com.kolmir.fitness_tracker.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.dto.jwt.JwtResponse;
import com.kolmir.fitness_tracker.dto.jwt.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.user.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.user.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.ConflictException;
import com.kolmir.fitness_tracker.exceptions.UnauthorizedException;
import com.kolmir.fitness_tracker.mappers.UserMapper;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.UserRepository;
import com.kolmir.fitness_tracker.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(UserLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new UnauthorizedException("невалидный токен");
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtils.generateAccessToken(username);

        return new JwtResponse(newAccessToken, refreshToken);
    }
    
    @Transactional
    public JwtResponse register(UserRegisterRequest request) {
        createUser(userMapper.toEntity(request));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String accessToken = jwtUtils.generateAccessToken(request.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(request.getUsername());

        return new JwtResponse(accessToken, refreshToken);
    }

    private void createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new ConflictException("пользователь с таким именем уже существует");
        if (userRepository.existsByEmail(user.getEmail()))
            throw new ConflictException("эта почта уже используется");
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
