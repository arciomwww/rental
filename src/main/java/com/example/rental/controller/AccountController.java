package com.example.rental.controller;

import com.example.rental.dto.AccountDto;
import com.example.rental.service.AccountService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Аккаунт пользователя(Баланс)")
@Validated
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Создание аккаунта пользователя (Доступ: Пользователь)",
            description = "Создание нового аккаунта для пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Аккаунт создан успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<AccountDto> createAccount(
            @Schema(
                    description = "Данные для создания аккаунта.",
                    example = "{ \"user_id\" : \"1\" }"
            )
            @Valid @RequestBody AccountDto accountDto
    ) {
        AccountDto createdAccount = accountService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @Operation(
            summary = "Чтение аккаунта пользователя (Доступ: Пользователь)",
            description = "Чтение информации об аккаунте пользователя по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/read/{id}")
    public ResponseEntity<AccountDto> getAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        AccountDto account = accountService.getAccount(id);
        return ResponseEntity.ok(account);
    }

    @Operation(
            summary = "Получение всех аккаунтов пользователей (Доступ: Администратор)",
            description = "Получение списка всех аккаунтов пользователей с возможностью пагинации"
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
    @GetMapping("/all")
    public ResponseEntity<Page<AccountDto>> getAllAccounts(
            @Schema(
                    description = "Параметры пагинации",
                    example = "\"?page=0&size=10\""
            )
            Pageable pageable
    ) {
        Page<AccountDto> accounts = accountService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Обновление аккаунта пользователя (Доступ: Администратор)",
            description = "Обновление информации об аккаунте пользователя по его ID"
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
    public ResponseEntity<AccountDto> updateAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "{ \"balance\": \"300.00\", \"user_id\": \"1\" }"

            )
            @PathVariable Long id,
            @Valid @RequestBody AccountDto accountDto
    ) {
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(
            summary = "Удаление аккаунта пользователя (Доступ: Администратор)",
            description = "Удаление аккаунта пользователя по его ID"
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
    public ResponseEntity<Void> deleteAccount(
            @Schema(
                    description = "ID аккаунта",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
