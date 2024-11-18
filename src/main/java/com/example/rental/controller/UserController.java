package com.example.rental.controller;

import com.example.rental.dto.UserDto;
import com.example.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Управление пользователями")
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получение пользователя по ID (Доступ: Администратор и Пользователь)",
            description = "Получение пользователя по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/{id}")
    @PreAuthorize("hasAnyRole(\"USER\", \"ADMIN\") and #id == authentication.principal.id")
    public ResponseEntity<UserDto> getUserById(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Обновление пользователя по ID (Доступ: Администратор и Пользователь)",
            description = "Обновление информации о пользователя по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (OK)", description = "Успешное обновление ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное обновление ресурса, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole(\"USER\", \"ADMIN\") and #id == authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @Schema(
            description = "ID",
            example = "1"
    ) @Valid @PathVariable Long id, @RequestBody UserDto userDto)
    {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Удаление пользователя по ID (Доступ: Администратор и Пользователь)",
            description = "Удаление информации о пользователя по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное удаление ресурса.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole(\"USER\", \"ADMIN\") and #id == authentication.principal.id")
    public ResponseEntity<?> deleteUser(
            @Schema(
                    description = "ID",
                    example = "1"
            )@PathVariable Long id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получение всех пользователя (Доступ: Администратор)",
            description = "Получение информации о всех пользователей с возможностью пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(@Schema(
            description = "Параметры пагинации",
            example = "\"?page=0&size=10\""
    )Pageable pageable)
    {
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователей по username (Доступ: Администратор)",
            description = "Получение пользователей по username"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByUsername(@Schema(
            description = "username",
            example = "arciomwww"
    )@PathVariable String username)
    {
        List<UserDto> users = userService.findByUsername(username);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователей по email (Доступ: Администратор)",
            description = "Получение пользователей по email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByEmail(@Schema(
            description = "email",
            example = "arciomwww@example.com"
    )@PathVariable String email)
    {
        List<UserDto> users = userService.findByEmail(email);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователей по имени (Доступ: Администратор)",
            description = "Получение пользователей по имени"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/firstName/{firstName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByFirstName(
            @Schema(
            description = "Имя",
            example = "Arciom"
    )@PathVariable String firstName)
    {
        List<UserDto> users = userService.findByFirstName(firstName);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователей по фамилии (Доступ: Администратор)",
            description = "Получение пользователей по фамилии"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/lastName/{lastName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByLastName(
            @Schema(
            description = "Фамилия",
            example = "Vyshynski"
    )@PathVariable String lastName)
    {
        List<UserDto> users = userService.findByLastName(lastName);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователя по enabled (Доступ: Администратор)",
            description = "Получение пользователя по enabled"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/enabled/{enabled}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByEnabled(@Schema(
            description = "Enabled",
            example = "true/false"
    ) @PathVariable Boolean enabled)
    {
        List<UserDto> users = userService.findByEnabled(enabled);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователя по enabled (Доступ: Администратор)",
            description = "Получение пользователя по enabled"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/enabledStatus/{enabled}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByEnabledStatusNative(
            @Schema(
            description = "Enabled",
            example = "true/false"
    )@PathVariable Boolean enabled)
    {
        List<UserDto> users = userService.findUsersByEnabledStatusNative(enabled);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Получение пользователя по Имени и Фамилии (Доступ: Администратор)",
            description = "Получение пользователя по Имени и Фамилии"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/filter/firstNameLastName")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByFirstNameAndLastNameJPQL(
            @Schema(
            description = "Имя и фамилия",
            example = "{\"first_name\": \"Arciom\", \"last_name\": \"Vyshynski\"}"
            )
            @RequestParam String firstName,
            @RequestParam String lastName)
    {
        List<UserDto> users = userService.findUsersByFirstNameAndLastNameJPQL(firstName, lastName);
        return ResponseEntity.ok(users);
    }
}
