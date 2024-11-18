package com.example.rental.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Table(name = "Users")
@Schema(description = "DTO пользователей")
public class UserDto {
    private Long id;

    @Schema(description = "Имя пользователя", example = "Arciomwww")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Пароль пользователя", example = "ccq338dd")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "Email пользователя", example = "arciomwww39@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Имя", example = "Arciom")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "Фамилия", example = "Vyshynski")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "Роль пользователя")
    private RoleDto role;

    @Schema(description = "Активирован ли пользователь", example = "true")
    @NotNull(message = "Enabled status is required")
    private Boolean enabled;
    @Schema(description = "Временная метка создания транзакции")
    private LocalDateTime createdAt;
    @Schema(description = "Временная метка обновления транзакции")
    private LocalDateTime updateAt;
}
