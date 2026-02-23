package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kolmir.fitness_tracker.dto.JwtResponse;
import com.kolmir.fitness_tracker.dto.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.JwtNotValidException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
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
    void login_GeneratesTokens() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("john");
        request.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn((Authentication) null);
        when(jwtUtils.generateAccessToken("john")).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken("john")).thenReturn("refresh-token");

        JwtResponse response = authService.login(request);

        assertEquals("access-token", response.getAccess());
        assertEquals("refresh-token", response.getRefresh());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void refreshToken_WhenValid_ReturnsNewAccessToken() throws JwtNotValidException {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");

        when(jwtUtils.validateToken("refresh-token")).thenReturn(true);
        when(jwtUtils.getUsernameFromToken("refresh-token")).thenReturn("john");
        when(jwtUtils.generateAccessToken("john")).thenReturn("new-access");

        JwtResponse response = authService.refreshToken(request);

        assertEquals("new-access", response.getAccess());
        assertEquals("refresh-token", response.getRefresh());
    }

    @Test
    void refreshToken_WhenInvalid_Throws() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("bad-token");

        when(jwtUtils.validateToken("bad-token")).thenReturn(false);

        assertThrows(JwtNotValidException.class, () -> authService.refreshToken(request));
    }

    @Test
    void register_WhenUsernameExists_Throws() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setEmail("mail@test.com");

        User user = new User();
        user.setUsername("john");
        user.setEmail("mail@test.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void register_WhenEmailExists_Throws() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setEmail("mail@test.com");

        User user = new User();
        user.setUsername("john");
        user.setEmail("mail@test.com");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("mail@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyInUseException.class, () -> authService.register(request));
    }

    @Test
    void register_WhenSuccessful_ReturnsTokens() throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setEmail("mail@test.com");

        User user = new User();
        user.setUsername("john");
        user.setEmail("mail@test.com");
        user.setPassword("password");

        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("mail@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn((Authentication) null);
        when(jwtUtils.generateAccessToken("john")).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken("john")).thenReturn("refresh-token");

        JwtResponse response = authService.register(request);

        assertEquals("access-token", response.getAccess());
        assertEquals("refresh-token", response.getRefresh());
        verify(userRepository).save(user);
        assertEquals("encoded", user.getPassword());
    }
}
