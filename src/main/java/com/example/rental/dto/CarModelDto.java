package com.example.rental.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO моделей автомобилей")
public class CarModelDto {
    @Schema(description = "Уникальный идентификатор модели автомобиля", example = "1")
    private Long id;
    @NotBlank(message = "Brand is required")
    @Schema(description = "Бренд автомобиля", example = "BMW")
    private String brand;

    @NotBlank(message = "Model is required")
    @Schema(description = "Модель автомобиля", example = "X5M")
    private String model;

    @Min(value = 2000, message = "Year should be valid")
    @Schema(description = "Год выпуска автомобиля", example = "от 2000 (2024)")
    private int year;
}
