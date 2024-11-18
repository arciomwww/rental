package com.example.rental.controller;

import com.example.rental.dto.LocationDto;
import com.example.rental.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "Управление местоположениями(локациями)")
@Slf4j
@RestController
@RequestMapping("/api/locations")
@Validated
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(
            summary = "Cоздание локации (Доступ: Администратор)",
            description = "Создание локации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 (Created)", description = "Локация создана успешно!",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createLocation(
            @Schema(
            description = "Данные для создания локации",
            example = "{ \"parking_lot\": \"6\", \"address\": \"Pobeda 13\", \"city\": \"Minsk\", \"country\": \"Belarus\"} \""
    )@Valid @RequestBody LocationDto locationDto)
    {
        try {
            locationService.createLocation(locationDto);
            return ResponseEntity.ok("Location created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(
            summary = "Получение всех локаций (Доступ: Администратор и Пользователь)",
            description = "Получение всех локаций с возможностью пагинации"
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
    public ResponseEntity<Page<LocationDto>> getAllLocations(
            @Schema(
            description = "Параметры пагинации",
            example = "\"?page=0&size=10\""
    )
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Page<LocationDto> locations = locationService.getAllLocations(page, size);
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }


    @Operation(
            summary = "Получение локации по ID (Доступ: Администратор и Пользователь)",
            description = "Получение локации по ID"
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
    public ResponseEntity<LocationDto> getLocationById(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        LocationDto locationDto = locationService.getLocationById(id);
        return new ResponseEntity<>(locationDto, HttpStatus.OK);
    }


    @Operation(
            summary = "Обновление локации (Доступ: Администратор)",
            description = "Обновление локации"
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
    public ResponseEntity<LocationDto> updateLocation(
            @Schema(
            description = "Данные для регистрации пользователя",
                    example = "{ \"parking_lot\": \"6\", \"address\": \"Pobeda 13\", \"city\": \"Minsk\", \"country\": \"Belarus\"} \""
    )@Valid @PathVariable Long id, @RequestBody  LocationDto locationDto)
    {
        LocationDto updatedLocation = locationService.updateLocation(id, locationDto);
        return new ResponseEntity<>(updatedLocation, HttpStatus.OK);
    }


    @Operation(
            summary = "Удаление локации по ID (Доступ: Администратор)",
            description = "Удаление локации по ID"
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
    public ResponseEntity<Void> deleteLocation(
            @Schema(
            description = "ID",
            example = "1"
    )@PathVariable Long id)
    {
        locationService.deleteLocation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Operation(
            summary = "Получение локации по городу (Доступ: Администратор и Пользователь)",
            description = "Получение локации по городу"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/city/{city}")
    public ResponseEntity<List<LocationDto>> getLocationsByCity(
            @Schema(
            description = "Город",
            example = "Brest"
    )@PathVariable String city)
    {
        List<LocationDto> locations = locationService.getLocationsByCity(city);
        log.info("Retrieved locations in city: {}", city);
        return ResponseEntity.ok(locations);
    }


    @Operation(
            summary = "Получение всех локаций по стране (Доступ: Администратор и Пользователь)",
            description = "Получение всех локаций по стране"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/country/{country}")
    public ResponseEntity<List<LocationDto>> getLocationsByCountry(
            @Schema(
            description = "Страна",
            example = "Belarus"
    )@PathVariable String country)
    {
        List<LocationDto> locations = locationService.getLocationsByCountry(country);
        log.info("Retrieved locations in country: {}", country);
        return ResponseEntity.ok(locations);
    }


    @Operation(
            summary = "Получение локации по ID автомобилю (Доступ: Администратор и Пользователь)",
            description = "Получение локации по ID автомобилю"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200 (ОК)", description = "Успешное получение ресурса.",content = @Content),
            @ApiResponse(responseCode = "204 (No Content)", description = "Запрос выполнен успешно, но возвращаемого контента нет.",content = @Content),
            @ApiResponse(responseCode = "400 (Bad Request)", description = "Некорректный запрос от клиента.",content = @Content),
            @ApiResponse(responseCode = "401 (Unauthorized)", description = "Требуется аутентификация.",content = @Content),
            @ApiResponse(responseCode = "403 (Forbidden)", description = "Доступ запрещен.",content = @Content),
            @ApiResponse(responseCode = "404 (Not Found)", description = "Ресурс не найден.",content = @Content),
    })
    @GetMapping("/read/location-by-car/{carId}")
    public ResponseEntity<LocationDto> getLocationByCarId(
            @Schema(
            description = "Car_id",
            example = "1"
    )@PathVariable Long carId)
    {
        LocationDto locationDto = locationService.getLocationByCarId(carId);
        return new ResponseEntity<>(locationDto, HttpStatus.OK);
    }

}
