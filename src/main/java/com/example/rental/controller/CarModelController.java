package com.example.rental.controller;

import com.example.rental.dto.CarModelDto;
import com.example.rental.service.CarModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

@Tag(name = "Управление модельным рядом автомобилей")
@Validated
@RestController
@RequestMapping("/api/car-models")
public class CarModelController {
    private final CarModelService carModelService;

    @Autowired
    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @Operation(
            summary = "Создание новой модели автомобиля (Доступ: Администратор)",
            description = "Создание новой модели автомобиля"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Модель автомобиля создана успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createCarModel(@Valid @RequestBody
                                                @Schema(
            description = "Данные для создания модели",
            example = "{ \"brand\": \"Toyota\", \"model\": \"Corolla\", \"year\": \"2023\"}"
    )CarModelDto carModelDto)
    {
        CarModelDto created = carModelService.createCarModel(carModelDto);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Обновление существующей модели автомобиля (Доступ: Администратор)",
            description = "Обновление существующей модели автомобиля"
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
    public ResponseEntity<CarModelDto> updateCarModel(
            @Schema(
                    description = "Обновление модели модели",
                    example = "{ \"brand\": Toyota, \"model\": Corolla, \"year\": \"2024\"}"
            ) @Valid @PathVariable Long id, @RequestBody CarModelDto carModelDto)
    {
        return ResponseEntity.ok(carModelService.updateCarModel(id, carModelDto));
    }

    @Operation(
            summary = "Удаление модели (Доступ: Администратор)",
            description = "Удаление модели автомобиля"
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
    public ResponseEntity<Void> deleteCarModel(
            @Schema(
                    description = "ID",
                    example = "1"
            ) @PathVariable Long id)
    {
        carModelService.deleteCarModel(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Получение модели автомобиля по ID (Доступ: Администратор и Пользователь)",
            description = "Получение информации о модели автомобиля по ее идентификатору"
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
    public ResponseEntity<CarModelDto> getCarModelById(
            @Schema(
                    description = "ID",
                    example = "1"
            )@PathVariable Long id)
    {
        return ResponseEntity.ok(carModelService.getCarModelById(id));
    }
}


