package com.kolmir.fitness_tracker.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kolmir.fitness_tracker.models.User;

public class CurrentUserProvider {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) 
            throw new AccessDeniedException("текущий пользователь не найден");

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User))
            throw new AccessDeniedException("текущий пользователь не найден");
        
        return ((User)principal).getId();
    }
}