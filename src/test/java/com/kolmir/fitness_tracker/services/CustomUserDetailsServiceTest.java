package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loadUserByUsernameReturnsUserWhenFound() {
        User user = new User();
        user.setUsername("kolmir");

        when(userRepository.getUserByUsername("kolmir")).thenReturn(Optional.of(user));

        var result = customUserDetailsService.loadUserByUsername("kolmir");

        assertSame(user, result);
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.getUserByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing"));
    }

    @Test
    void getCurrentUserIdReturnsIdWhenPrincipalIsUser() {
        User user = new User();
        user.setId(77L);

        var authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long result = customUserDetailsService.getCurrentUserId();

        assertEquals(77L, result);
    }

    @Test
    void getCurrentUserIdReturnsNullWhenNoAuthentication() {
        SecurityContextHolder.clearContext();

        Long result = customUserDetailsService.getCurrentUserId();

        assertNull(result);
    }

    @Test
    void getCurrentUserIdReturnsNullWhenPrincipalIsNotUser() {
        var authentication = new UsernamePasswordAuthenticationToken("anonymous", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long result = customUserDetailsService.getCurrentUserId();

        assertNull(result);
    }
}
