package com.example.rental.controller;

import com.example.rental.dto.UserRolesDto;
import com.example.rental.service.UserRolesService;
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

@Tag(name = "Пользователи и их роли")
@Validated
@RestController
@RequestMapping("/user-roles")
public class UserRolesController {
    private final UserRolesService userRolesService;

    @Autowired
    public UserRolesController(UserRolesService userRolesService) {
        this.userRolesService = userRolesService;
    }

    @Operation(
            summary = "Получение всех пользователей с их ролями (Доступ: Администратор)",
            description = "Получение всех пользователей с их ролями и с возможностью пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/read/all")
    public ResponseEntity<Page<UserRolesDto>> getAllUserRoles(
            @Schema(
                    description = "Параметры пагинации",
                    example = "\"?page=0&size=10\""
            )
            Pageable pageable)
    {
        Page<UserRolesDto> userRolesPage = userRolesService.getAllUserRoles(pageable);
        return ResponseEntity.ok(userRolesPage);
    }

    @Operation(
            summary = "Чтение пользователя и его роли по ID (Доступ: Администратор)",
            description = "Получение информации пользователя и его роли по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/read/{id}")
    public ResponseEntity<UserRolesDto> getUserRoleById(
            @Schema(description = "ID",
                    example = "1")
            @PathVariable Long id)
    {
        UserRolesDto userRole = userRolesService.getUserRoleById(id);
        return userRole != null ? ResponseEntity.ok(userRole) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Обновление роли пользователя (Доступ: Администратор)",
            description = "Обновление информации о роли пользователя по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (OK)", description = "Успешное обновление ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное обновление ресурса, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserRolesDto> updateUserRole(
            @Schema(
                    description = "ID",
                    example = "{ \"user_id\": \"1\", \"role_id\": \"1\" } \""
            )
            @PathVariable Long id,
            @Valid @RequestBody UserRolesDto userRolesDto)
    {
        UserRolesDto updatedUserRole = userRolesService.updateUserRole(id, userRolesDto);
        return updatedUserRole != null ? ResponseEntity.ok(updatedUserRole) : ResponseEntity.notFound().build();
    }


    @Operation(
            summary = "Удаление информации о пользователи и его роли (Доступ: Администратор)",
            description = "Удаление информации о пользователи и его роли по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное удаление ресурса.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserRole(
            @Schema(description = "ID ",
                    example = "1")
            @PathVariable Long id)
    {
        boolean deleted = userRolesService.deleteUserRole(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
