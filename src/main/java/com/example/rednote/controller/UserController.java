package com.example.rednote.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rednote.model.dto.PasswordUpdateDTO;
import com.example.rednote.model.dto.UserUpdateDTO;
import com.example.rednote.model.vo.PostVO;
import com.example.rednote.model.vo.UserDetailsVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.example.rednote.common.response.Result;
import com.example.rednote.model.dto.LoginDTO;
import com.example.rednote.model.dto.UserDTO;
import com.example.rednote.model.vo.UserVO;
import com.example.rednote.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "用户接口", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Result<UserVO> getById(@PathVariable Integer userId) {
        return Result.success(userService.getById(userId));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    public Result<?> register(@RequestBody UserDTO userDto) {
        if (userService.register(userDto) == 1) {
            return Result.success();
        } else {
            return Result.fail("注册失败");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "登录用户账号")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户详情")
    public Result<UserDetailsVO> getCurrentUser() {
        return Result.success(userService.getCurrentUserDetails());
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public Result<Boolean> updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        return Result.success(userService.updateUserInfo(updateDTO));
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Boolean> updatePassword(@Valid @RequestBody PasswordUpdateDTO passwordDTO) {
        return Result.success(userService.updatePassword(passwordDTO));
    }

    @PostMapping("/follow/{followUserId}")
    @Operation(summary = "关注/取消关注用户")
    public Result<Boolean> followUser(@PathVariable Integer followUserId) {
        boolean isFollow = userService.followUser(followUserId);
        return Result.success(isFollow);
    }

    @GetMapping("/follow/{followUserId}")
    @Operation(summary = "判断是否关注用户")
    public Result<Boolean> isFollow(@PathVariable Integer followUserId) {
        Boolean isFollow = userService.isFollow(followUserId);
        return Result.success(isFollow);
    }

    @GetMapping("/{userId}/posts")
    @Operation(summary = "查询用户发布的帖子列表")
    public Result<Page<PostVO>> listUserPosts(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(userService.listUserPosts(userId, page, size));
    }

    @GetMapping("/followers")
    @Operation(summary = "查询关注的人")
    public Result<List<UserVO>> listFollowers() {
        return Result.success(userService.listBloggers());
    }

    @GetMapping("/current/me")
    @Operation(summary = "获取当前登录用户",description = "返回当前登录用户的简略信息")
    public Result<UserVO> getCurrentUserInfo() {
        return Result.success(userService.getCurrentUser());
    }
}
