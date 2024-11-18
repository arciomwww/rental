package com.example.rental.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO местоположений стоянок автомобилей")
public class LocationDto {
    @Schema(description = "Уникальный идентификатор местоположения", example = "1")
    private Long id;
    @NotBlank(message = "Parking lot is required")
    @Schema(description = "Номер места на парковке", example = "1")
    private String parkingLot;

    @NotBlank(message = "Address is required")
    @Schema(description = "Адрес, где располагается стоянка", example = "ул. Московская 267")
    private String address;

    @NotBlank(message = "City is required")
    @Schema(description = "Город, где располагается стоянка", example = "Брест")
    private String city;

    @NotBlank(message = "Country is required")
    @Schema(description = "Страна, где располагается стоянка",example = "Беларусь")
    private String country;

    @Schema(description = "Автомобиль")
    private List<CarDto> cars;
}
