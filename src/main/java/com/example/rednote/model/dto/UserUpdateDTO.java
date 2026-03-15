package com.example.rednote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息更新DTO")
public class UserUpdateDTO {
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatar;
    @Schema(description = "性别 1-男 2-女 0-未知")
    private Integer gender;
    @Schema(description = "个人简介")
    private String bio;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
}