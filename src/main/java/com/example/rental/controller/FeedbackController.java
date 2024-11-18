package com.example.rental.controller;

import com.example.rental.dto.FeedbackDto;
import com.example.rental.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Tag(name = "Управление отзывами")
@RestController
@RequestMapping("/api/feedback")
@Validated
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }


    @Operation(
            summary = "Создание отзыва (Доступ: Пользователь)",
            description = "Создание отзыва"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Отзыв создан успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createFeedback(
            @Schema(
            description = "Данные для создания отзыва",
            example = "{ \"user_id\": \"1\", \"car_id\": \"1\", \"rating\": \"5\", \"comment\": \"Невероятно!\" } \""
    )@Valid @RequestBody FeedbackDto feedbackDto)
    {
        try {
            feedbackService.createFeedback(feedbackDto);
            return ResponseEntity.ok("Feedback created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Обновление отзыва (Доступ: Пользователь)",
            description = "Обновление отзыва"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (OK)", description = "Успешное обновление ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное обновление ресурса, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<FeedbackDto> updateFeedback(
            @Schema(
            description = "ID",
                    example = "{ \"user_id\": \"1\", \"car_id\": \"1\", \"rating\": \"5\", \"comment\": \"Невероятно!\" } \""
    )@Valid @PathVariable Long id, @RequestBody FeedbackDto feedbackDto)
    {
        FeedbackDto updatedFeedback = feedbackService.updateFeedback(id, feedbackDto);
        return new ResponseEntity<>(updatedFeedback, HttpStatus.OK);
    }


    @Operation(
            summary = "Удаление отзыва (Доступ: Пользователь)",
            description = "Удаление отзыва по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное удаление ресурса.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFeedback(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        feedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "Получение всех отзывов (Доступ: Администратор и Пользователь)",
            description = "Получение всех отзывов с возможностью пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/all")
    public ResponseEntity<Page<FeedbackDto>> getAllFeedback(
            @Schema(
            description = "Параметры пагинации",
            example = "\"?page=0&size=10\""
    )
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<FeedbackDto> feedbackPage = feedbackService.getAllFeedback(pageable);
        return new ResponseEntity<>(feedbackPage, HttpStatus.OK);
    }


    @Operation(
            summary = "Получение отзыва по ID (Доступ: Администратор и Пользователь)",
            description = "Получение отзыва по ID"
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
    public ResponseEntity<FeedbackDto> getFeedbackById(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        FeedbackDto feedbackDto = feedbackService.getFeedbackById(id);
        return new ResponseEntity<>(feedbackDto, HttpStatus.OK);
    }
}
