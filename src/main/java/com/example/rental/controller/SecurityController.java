package com.example.rental.controller;

import com.example.rental.dto.UserDto;
import com.example.rental.security.JwtCore;
import com.example.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

@Tag(name = "Регистрация и Авторизация")
@Validated
@RestController
@RequestMapping("/auth")
public class SecurityController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    @Autowired
    public SecurityController(UserService userService, @Lazy AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован", content = @Content),
            @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Schema(
            description = "Данные для регистрации пользователя",
            example = "{ \"username\": \"Arciomwww\", \"password\": \"password123\", \"email\": \"arciomwww@example.com\", \"first_name\": \"Arciom\", \"last_name\": \"Vyshynski\", \"enabled\": true, \"role\": { \"name\": \"ROLE_ADMIN\" } } \""
    )@Valid @RequestBody UserDto userDto)
    {
        userService.registerNewUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация успешна", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content)
    })
    @PostMapping("/signing")
    public ResponseEntity<?> signing(
            @Schema(
            description = "Данные для регистрации пользователя",
            example = "{ \"username\": \"Arciomwww\", \"password\": \"password123\" } \""
    ) @Valid @RequestBody UserDto userDto)
    {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(), userDto.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
}


