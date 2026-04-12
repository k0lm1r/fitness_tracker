package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginAuthenticatesAndReturnsTokens() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user1");
        request.setPassword("pass1");

        when(jwtUtils.generateAccessToken("user1")).thenReturn("access");
        when(jwtUtils.generateRefreshToken("user1")).thenReturn("refresh");

        JwtResponse result = authService.login(request);

        assertEquals("access", result.getAccess());
        assertEquals("refresh", result.getRefresh());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshTokenThrowsWhenTokenIsInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("bad-token");

        when(jwtUtils.validateToken("bad-token")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshTokenReturnsNewAccessTokenWhenValid() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(jwtUtils.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtils.getUsernameFromToken("refresh-token")).thenReturn("user2");
        when(jwtUtils.generateAccessToken("user2")).thenReturn("new-access");

        JwtResponse result = authService.refreshToken(request);

        assertEquals("new-access", result.getAccess());
        assertEquals("refresh-token", result.getRefresh());
    }

    @Test
    void registerThrowsWhenUsernameExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("taken");
        request.setPassword("pass");

        User user = new User();
        user.setUsername("taken");
        user.setEmail("mail@example.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void registerThrowsWhenEmailExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user3");
        request.setPassword("pass");

        User user = new User();
        user.setUsername("user3");
        user.setEmail("used@example.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("user3")).thenReturn(false);
        when(userRepository.existsByEmail("used@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void registerCreatesUserAndReturnsTokens() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("user4");
        request.setPassword("raw-pass");

        User user = new User();
        user.setUsername("user4");
        user.setPassword("raw-pass");
        user.setEmail("user4@example.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("user4")).thenReturn(false);
        when(userRepository.existsByEmail("user4@example.com")).thenReturn(false);
        when(passwordEncoder.encode("raw-pass")).thenReturn("encoded-pass");
        when(jwtUtils.generateAccessToken("user4")).thenReturn("access-4");
        when(jwtUtils.generateRefreshToken("user4")).thenReturn("refresh-4");

        JwtResponse result = authService.register(request);

        assertEquals("access-4", result.getAccess());
        assertEquals("refresh-4", result.getRefresh());
        assertEquals("encoded-pass", user.getPassword());
        verify(userRepository).save(user);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
