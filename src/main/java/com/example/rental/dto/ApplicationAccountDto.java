package com.example.rental.dto;

import java.math.BigDecimal;

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
@Schema(description = "DTO аккаунта приложения")
public class ApplicationAccountDto {
    @Schema(description = "Уникальный идентификатор аккаунта приложения", example = "1")
    private Long id;
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Balance must be positive")
    @Schema(description = "Количество средств на счету аккаунта приложения", example = "1000.00")
    private BigDecimal balance;
}
