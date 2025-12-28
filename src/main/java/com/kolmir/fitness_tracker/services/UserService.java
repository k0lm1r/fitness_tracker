package com.kolmir.fitness_tracker.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.models.User;
import com.kolmir.fitness_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void createUser(User user) throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameAlreadyExistsException("пользователь с таким именем уже существует");
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyInUseException("эта почта уже используется");
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override 
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username).orElseThrow (
                () -> new UsernameNotFoundException("пользователь с таким именем не найден")
        );
    }
}
