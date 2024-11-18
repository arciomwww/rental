package com.example.rental.controller;

import com.example.rental.dto.RentalDto;
import com.example.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@Tag(name = "Управление арендой автомобилей")
@RestController
@RequestMapping("/api/rentals")
@Validated
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }


    @Operation(
            summary = "Создание аренды (Доступ: Администратор и Пользователь)",
            description = "Создание аренды"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Аренда создана успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<?> createRental(
            @Schema(
            description = "Данные для создании аренды",
            example = "{ \"car_id\": \"1\", \"user_id\": \"1\", \"tariff_type\": \"HOURLY|SUBSCRIPTION\", \"hourly_rate\": \"15.50\", \"subscription_rate\": \"null|200.00\", \"discount\": \"10.0\", \"mileage\": \"100\", \"additional_info\": \"оплата за камри\" } \""
    )@Valid @RequestBody RentalDto rentalDto)
    {
        try {
            rentalService.createRental(rentalDto);
            return ResponseEntity.ok("Rental created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Обновление аренды (Доступ: Администратор)",
            description = "Обновлении аренды"
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
    public ResponseEntity<RentalDto> updateRental(
            @Schema(
            description = "Изменённые данные",
                    example = "{ \"car_id\": \"1\", \"user_id\": \"1\", \"tariff_type\": \"HOURLY|SUBSCRIPTION\", \"hourly_rate\": \"15.50\", \"subscription_rate\": \"null|200.00\", \"discount\": \"10.0\", \"mileage\": \"100\", \"additional_info\": \"оплата за камри\" } \""
    )@Valid @PathVariable Long id, @RequestBody RentalDto rentalDto)
    {
        RentalDto updateRental = rentalService.updateRental(id, rentalDto);
        return new ResponseEntity<>(updateRental, HttpStatus.OK);
    }


    @Operation(
            summary = "Удаление аренды (Доступ: Администратор)",
            description = "Удаление аренды по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 (No Content)", description = "Успешное удаление ресурса.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteRental(
            @Schema(
            description = "ID",
            example = "1"
    ) @PathVariable Long id)
    {
        rentalService.deleteRental(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "Получение всех аренд (Доступ: Администратор)",
            description = "Получение всех аренд с возможностью пагинации"
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
    public ResponseEntity<Page<RentalDto>> getAllRentals(
            @Schema(
            description = "Параметры пагинации",
            example = "\"?page=0&size=10\""
    )@PageableDefault(size = 10) Pageable pageable)
    {
        Page<RentalDto> rentals = rentalService.getAllRentals(pageable);
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }


    @Operation(
            summary = "Получение аренды по ID (Доступ: Администратор)",
            description = "Получение аренды по ID"
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
    public ResponseEntity<RentalDto> getRentalById(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        RentalDto rentalDto = rentalService.getRentalById(id);
        return new ResponseEntity<>(rentalDto, HttpStatus.OK);
    }


    @Operation(
            summary = "Получение истории аренд пользователя (Доступ: Пользователь)",
            description = "Получение истории аренд по user_id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user/{userId}/history")
    public List<RentalDto> getUserRentalHistory(
            @Schema(
            description = "User_id",
            example = "1"
    )@PathVariable Long userId)
    {
        return rentalService.getUserRentalHistory(userId);
    }


    @Operation(
            summary = "Получение истории всех аренд по конкретной машине (Доступ: Администратор)",
            description = "Получение истории всех аренд по конкретной машине"
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
    @GetMapping("admin/car/{carId}")
    public ResponseEntity<List<RentalDto>> getRentalsByCarId(
            @Schema(
            description = "Car_id",
            example = "1"
    )@PathVariable Long carId, Pageable pageable)
    {
        List<RentalDto> rentals = rentalService.getRentalsByCarId(carId, pageable);
        return ResponseEntity.ok(rentals);
    }
}
