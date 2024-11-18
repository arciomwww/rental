package com.example.rental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO аренды автомобилей")
public class RentalDto {
    @Schema(description = "Уникальный идентификатор аренды", example = "1")
    private Long id;
    @NotNull(message = "Car ID is required")
    @Schema(description = "Идентификатор автомобиля", example = "1")
    private Long carId;

    @NotNull(message = "User ID is required")
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long userId;

    @NotNull(message = "Start date is required")
    @Schema(description = "Временная метка начало аренды автомобиля")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Временная метка завершения аренды автомобиля")
    private LocalDateTime endDate;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be positive")
    @Schema(description = "Итоговая цена за аренду", example = "100.00")
    private BigDecimal totalPrice;
    @Schema(description = "Километраж", example = "100")
    private int mileage;
    @Schema(description = "Дополнительная информация", example = "Оплата за камри")
    private String additionalInfo;
    @Schema(description = "Тип тарифа", example = "\"HOURLY\" или \"SUBSCRIPTION\"")
    private String tariffType; // "HOURLY" или "SUBSCRIPTION"
    @Schema(description = "Количество часов", example = "5")
    private BigDecimal hourlyRate;
    @Schema(description = "Количество дней", example = "2")
    private BigDecimal subscriptionRate;
    @Schema(description = "Скидка", example = "10")
    private BigDecimal discount;
}
