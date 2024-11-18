package com.example.rental.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO ролей пользователей")
public class UserRolesDto {
    @Schema(description = "Уникальный идентификатор ролей пользователя", example = "1")
    private Long id;

    @NotNull(message = "User ID is required")
    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long userId;

    @NotNull(message = "Role ID is required")
    @Schema(description = "Идентификатор роли пользователя", example = "1")
    private Long roleId;
}
