package com.example.rental.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "DTO ролей приложения")
public class RoleDto {
    @Schema(description = "Уникальный идентификатор роли пользователя", example = "1")
    private Long id;
    @NotBlank(message = "Role name is required")
    @Schema(description = "Роль пользователя", example = "ROLE_ADMIN / ROLE_USER")
    private String name;
}
