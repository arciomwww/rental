package com.example.rental.controller;

import com.example.rental.dto.CarDto;
import com.example.rental.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Tag(name = "Управления автомобилями")
@RestController
@RequestMapping("/api/cars")
@Validated
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService){
        this.carService = carService;
    }


    @Operation(
            summary = "Создание автомобиля (Доступ: Администратор)",
            description = "Создание нового автомобиля"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Автомобиль создан успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createCar(
            @Schema(
                    description = "Данные для создания автомобиля",
                    example = "{ \"model_id\": 1, \"location_id\": 2, \"status\": \"available\", \"price_per_hour\": 10.0, \"price_per_day\": 100.0 }"
            )
            @Valid @RequestBody CarDto carDto
    ) {
        try {
            carService.createCar(carDto);
            return ResponseEntity.ok("Car created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
            summary = "Обновление автомобиля (Доступ: Администратор)",
            description = "Обновление информации об автомобиле по его ID"
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
    public ResponseEntity<CarDto> updateCar(
            @Schema(
                    description = "ID автомобиля",
                    example = "{ \"model_id\": 1, \"location_id\": 2, \"status\": \"not available\", \"price_per_hour\": 10.0, \"price_per_day\": 100.0 }"
            )
            @PathVariable Long id,
            @Valid @RequestBody CarDto carDto
    ) {
        CarDto updateCar = carService.updateCar(id, carDto);
        return new ResponseEntity<>(updateCar, HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление автомобиля (Доступ: Администратор)",
            description = "Удаление автомобиля по его ID"
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
    public ResponseEntity<Void> deleteCar(
            @Schema(
                    description = "ID автомобиля",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Получение всех автомобилей (Доступ: Администратор и Пользователь)",
            description = "Получение списка всех автомобилей с возможностью пагинации"
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
    public ResponseEntity<Page<CarDto>> getAllCars(
            @Parameter(
                    description = "Параметры пагинации",
                    example = "\"?page=0&size=10\""
            )
            Pageable pageable
    ) {
        Page<CarDto> cars = carService.getAllCars(pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @Operation(
            summary = "Получение автомобиля по ID (Доступ: Администратор и Пользователь)",
            description = "Получение информации об автомобиле по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/{id}" )
    public ResponseEntity<CarDto> getCarById(
            @Schema(
                    description = "ID автомобиля",
                    example = "1"
            )
            @PathVariable Long id
    ) {
        CarDto carDto = carService.getCarById(id);
        return new ResponseEntity<>(carDto, HttpStatus.OK);
    }
}


