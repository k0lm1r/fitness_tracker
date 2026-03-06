package com.kolmir.fitness_tracker.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginRequest {
    @NotEmpty(message = "имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "длина имени пользователя должна быть от 3 до 50 символов")
    private String username;
    
    @NotEmpty(message = "пароль не может быть пустым")
    @Size(min = 4, max = 100, message = "длина пароля должна быть от 4 до 100 символов")
    private String password;
}