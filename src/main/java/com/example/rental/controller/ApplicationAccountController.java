package com.example.rental.controller;

import com.example.rental.dto.ApplicationAccountDto;
import com.example.rental.service.ApplicationAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

@Tag(name = "Аккаунт приложения(Баланс)")
@Validated
@RestController
@RequestMapping("/api/application-accounts")
public class ApplicationAccountController {

    private final ApplicationAccountService applicationAccountService;

    @Autowired
    public ApplicationAccountController(ApplicationAccountService applicationAccountService) {
        this.applicationAccountService = applicationAccountService;
    }

    @Operation(
            summary = "Создание аккаунта приложения (Доступ: Администратор)",
            description = "Создание нового аккаунта для приложения"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Аккаунт приложения создан успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApplicationAccountDto> createApplicationAccount(
            @Schema(
                    description = "Данные для создания аккаунта приложения",
                    example = "{ \"balance\": 100.00 }"
            )
            @Valid @RequestBody ApplicationAccountDto applicationAccountDto
    ) {
        ApplicationAccountDto createdAccount = applicationAccountService.createApplicationAccount(applicationAccountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @Operation(
            summary = "Получение аккаунта приложения (Доступ: Администратор)",
            description = "Получение информации об аккаунте приложения по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/read/{id}")
    public ResponseEntity<ApplicationAccountDto> getApplicationAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        ApplicationAccountDto account = applicationAccountService.getApplicationAccount(id);
        return ResponseEntity.ok(account);
    }

    @Operation(
            summary = "Обновление аккаунта приложения (Доступ: Администратор)",
            description = "Обновление информации об аккаунте приложения по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (OK)", description = "Успешное обновление ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное обновление ресурса, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApplicationAccountDto> updateApplicationAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "{ \"balance\": 150.00 }"
            )
            @PathVariable Long id,
            @Valid @RequestBody ApplicationAccountDto applicationAccountDto
    ) {
        ApplicationAccountDto updatedAccount = applicationAccountService.updateApplicationAccount(id, applicationAccountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(
            summary = "Удаление аккаунта приложения (Доступ: Администратор)",
            description = "Удаление аккаунта приложения по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное удаление ресурса.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteApplicationAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        applicationAccountService.deleteApplicationAccount(id);
        return ResponseEntity.noContent().build();
    }
}
