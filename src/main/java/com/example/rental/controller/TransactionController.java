package com.example.rental.controller;

import com.example.rental.dto.TransactionDto;
import com.example.rental.service.TransactionService;
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

@Tag(name = "Управление транзакциями")
@Validated
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Оплата за аренду (Доступ: Пользователь)",
            description = "Перевод средств с баланса пользователя на баланс приложения."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Оплата прошла успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(
            @Schema(
            description = "Данные для регистрации пользователя (Пользователь)",
            example = "{ \"user_id\": \"1\", \"application_account_id\": \"1\", \"amount\": \"150.00\", \"type\": \"TRANSFER\", \"description\": \"Перевод средств за камри\" } \""
    )@Valid @RequestBody TransactionDto transactionDto)
    {
        transactionService.transferFunds(transactionDto);
        return ResponseEntity.ok("Перевод выполнен успешно!");
    }

    @Operation(
            summary = "Пополнение баланса аккаунта (Доступ: Пользователь)",
            description = "Пополнение средств на аккаунт пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Успешное пополнение.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public ResponseEntity<?> depositToUserAccount(
            @Schema(
            description = "Данные для пополнения баланса аккаунта пользователя.",
            example = "{ \"user_id\": \"1\", \"amount\": \"150.00\", \"type\": \"DEPOSIT\", \"description\": \"Пополнение счета\" } \""
    )@Valid @RequestBody TransactionDto transactionDto)
    {
        transactionService.depositToAccount(transactionDto);
        return ResponseEntity.ok("Счет пополнен успешно");
    }


    @Operation(
            summary = "Получение всех транзакций (Доступ: Администратор)",
            description = "Получение всех транзакций (оплаты и пополнения). "
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
    @GetMapping("read/all")
    public ResponseEntity<Page<TransactionDto>> getAllTransactions(@Schema(
            description = "Данные о всех транзакциях пользователя с возможностью пагинации.",
            example = "\"?page=0&size=10\""
    )Pageable pageable)
    {
        Page<TransactionDto> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
}
