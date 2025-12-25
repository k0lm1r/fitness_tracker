package com.kolmir.fitness_tracker.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void isUsernameTaken_DelegatesToRepository() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        userService.isUsernameTaken("john");

        verify(userRepository).existsByUsername("john");
    }

    @Test
    void isEmailTaken_DelegatesToRepository() {
        when(userRepository.existsByEmail("mail@test.com")).thenReturn(true);

        userService.isEmailTaken("mail@test.com");

        verify(userRepository).existsByEmail("mail@test.com");
    }

    @Test
    void createUser_WhenUsernameExists_Throws() {
        when(userRepository.existsByUsername("john")).thenReturn(true);
        User user = new User();
        user.setUsername("john");
        user.setEmail("a@b.com");

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_WhenEmailExists_Throws() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);
        User user = new User();
        user.setUsername("john");
        user.setEmail("a@b.com");

        assertThrows(EmailAlreadyInUseException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_EncodesPasswordAndSaves() throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        User user = new User();
        user.setUsername("john");
        user.setEmail("a@b.com");
        user.setPassword("raw");

        when(passwordEncoder.encode("raw")).thenReturn("encoded");

        userService.createUser(user);

        verify(passwordEncoder).encode("raw");
        verify(userRepository).save(user);
    }
}
