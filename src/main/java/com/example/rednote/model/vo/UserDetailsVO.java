package com.example.rednote.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsVO {
    private Integer userId;
    private String username;
    private String phone;
    private String email;
    private Integer gender;
    private String bio;
    private Integer followCount;
    private Integer fansCount;
    private Integer postCount;
    private Integer likeReceiveCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime createTime;
}