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
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO аккаунта пользователя")
public class AccountDto {
    @Schema(description = "Уникальный идентификатор аккаунта пользователя", example = "1")
    private Long id;
    @NotNull(message = "User ID is required")
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long userId;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be positive")
    @Schema(description = "Количество средств на счету аккаунта пользователя", example = "300.00")
    private BigDecimal balance;
}
