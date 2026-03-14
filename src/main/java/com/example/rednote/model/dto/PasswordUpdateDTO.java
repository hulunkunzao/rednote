package com.example.rednote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "密码修改DTO")
public class PasswordUpdateDTO {
    @NotBlank
    @Schema(description = "原密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;
    @NotBlank
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
    @NotBlank
    @Schema(description = "确认新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}