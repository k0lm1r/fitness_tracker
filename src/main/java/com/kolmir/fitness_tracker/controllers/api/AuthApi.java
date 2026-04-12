package com.kolmir.fitness_tracker.controllers.api;

import org.springframework.http.ResponseEntity;

import com.kolmir.fitness_tracker.dto.jwt.JwtResponse;
import com.kolmir.fitness_tracker.dto.jwt.RefreshTokenRequest;
import com.kolmir.fitness_tracker.dto.user.UserLoginRequest;
import com.kolmir.fitness_tracker.dto.user.UserRegisterRequest;
import com.kolmir.fitness_tracker.exceptions.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Аутентификация и регистрация")
public interface AuthApi {
    @Operation(summary = "Вход в систему")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
        @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<JwtResponse> signin(UserLoginRequest request);

    @Operation(summary = "Обновление access token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Токен обновлён"),
        @ApiResponse(responseCode = "401", description = "Refresh token невалиден",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<JwtResponse> refreshToken(RefreshTokenRequest request);

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован"),
        @ApiResponse(responseCode = "409", description = "Имя пользователя или email уже заняты",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<JwtResponse> signup(UserRegisterRequest request);
}
