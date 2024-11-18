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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO транзакций")
public class TransactionDto {
    @Schema(description = "Уникальный идентификатор транзакции", example = "1")
    private Long id;
    @NotNull(message = "User ID is required")
    @Schema(description = "Идентификатор пользователя, совершающего транзакцию", example = "1")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Schema(description = "Сумма транзакции, должна быть положительной", example = "150.00")
    private BigDecimal amount;

    @NotBlank(message = "Type is required")
    @Schema(description = "Тип транзакции (например, TRANSFER или DEPOSIT)", example = "TRANSFER")
    private String type;
    @Schema(description = "Временная метка создания транзакции")
    private LocalDateTime createdAt;
    @Schema(description = "Описание транзакции", example = "Перевод средств за аренду автомобиля")
    private String description;

    @NotNull(message = "Application account ID is required")
    @Schema(description = "Идентификатор счета приложения, на который происходит перевод", example = "1")
    private Long applicationAccountId;
}
