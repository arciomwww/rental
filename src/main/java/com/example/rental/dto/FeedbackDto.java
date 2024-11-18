package com.example.rental.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO отзывов")
public class FeedbackDto {
    @Schema(description = "Уникальный идентификатор отзыва", example = "1")
    private Long id;
    @NotNull(message = "User ID is required")
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long userId;

    @NotNull(message = "Car ID is required")
    @Schema(description = "Идентификатор автомобиля", example = "1")
    private Long carId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not be more than 5")
    @Schema(description = "Рейтинг за полученную услугу", example = "от 1 до 5")
    private int rating;
    @Schema(description = "Комментарий пользователя", example = "Спасибо, всё наивысшем уровне!")
    private String comment;
    @Schema(description = "Временная метка создания отзыва")
    private LocalDateTime createdAt;
}
