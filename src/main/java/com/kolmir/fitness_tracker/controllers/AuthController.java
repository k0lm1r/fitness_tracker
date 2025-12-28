package com.kolmir.fitness_tracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.kolmir.fitness_tracker.dto.JwtResponse;
import com.kolmir.fitness_tracker.dto.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.EmailAlreadyInUseException;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;
import com.kolmir.fitness_tracker.exceptions.JwtNotValidException;
import com.kolmir.fitness_tracker.exceptions.UsernameAlreadyExistsException;
import com.kolmir.fitness_tracker.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Аутентификация и управление токенами")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Вход по логину и паролю",
            description = "Возвращает access и refresh токены. Не требует авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учётные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserLoginRequest.class),
            examples = @ExampleObject(
                    name = "Логин",
                    value = """
                            {
                              "username": "user1",
                              "password": "password123"
                            }
                            """)))
    public ResponseEntity<?> signin(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Обновить access токен по refresh токену",
            description = "Принимает refresh токен и выдаёт новый access токен. Не требует авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен обновлён", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Не валидный токен", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = RefreshTokenRequest.class),
            examples = @ExampleObject(
                    name = "Обновление токена",
                    value = """
                            {
                              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            }
                            """)))
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) throws JwtNotValidException {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создаёт пользователя и возвращает access/refresh токены. Не требует авторизации.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь создан", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Имя или email уже заняты", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserRegisterRequest.class),
            examples = @ExampleObject(
                    name = "Регистрация",
                    value = """
                            {
                              "username": "newuser",
                              "password": "qwerty123",
                              "email": "user@example.com"
                            }
                            """)))
    public ResponseEntity<?> signup(@Valid @RequestBody UserRegisterRequest request) throws UsernameAlreadyExistsException, EmailAlreadyInUseException {
        return ResponseEntity.ok(authService.register(request));
    }
}
