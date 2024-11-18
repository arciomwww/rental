package com.example.rental.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO автомобилей")
public class CarDto {
    @Schema(description = "Уникальный идентификатор автомобиля", example = "1")
    private Long id;
    @NotNull(message = "Model ID is required")
    @Schema(description = "Идентификатор модели автомобиля", example = "1")
    private Long modelId;

    @NotNull(message = "Location ID is required")
    @Schema(description = "Идентификатор местоположения автомобиля", example = "1")
    private Long locationId;

    @NotBlank(message = "Status is required")
    @Schema(description = "Статус автомобиля", example = "доступный / не доступный")
    private String status;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per hour must be positive")
    @Schema(description = "Стоимость аренды за час", example = "100.00")
    private BigDecimal pricePerHour;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per day must be positive")
    @Schema(description = "Стоимость аренды за сутки", example = "600.00")
    private BigDecimal pricePerDay;
}
