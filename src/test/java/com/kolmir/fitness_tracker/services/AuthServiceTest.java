package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUser() {
        User user = new User();
        when(userRepository.getUserByUsername("john")).thenReturn(Optional.of(user));

        assertSame(user, authService.loadUserByUsername("john"));
    }

    @Test
    void loadUserByUsername_WhenMissing_Throws() {
        when(userRepository.getUserByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.loadUserByUsername("unknown"));
    }
}
