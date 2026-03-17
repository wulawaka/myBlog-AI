package com.example.my_blog.controller;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.LoginRequest;
import com.example.my_blog.dto.RegisterRequest;
import com.example.my_blog.dto.ChangePasswordRequest;
import com.example.my_blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 允许跨域访问
public class UserController {

    private final UserService userService;

    /**
     * 用户登录接口
     * @param username 用户名（必填，3-6位字母数字下划线）
     * @param password 密码（必填，至少6位）
     * @return 登录结果JSON
     */
    @PostMapping("/login")
    public Object login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("收到登录请求，用户名：{}", loginRequest.getUsername());
        return userService.login(loginRequest);
    }

    /**
     * 用户注册接口
     * @param username 用户名（必填，3-6位字母数字下划线，唯一）
     * @param email 邮箱（必填，标准邮箱格式，唯一）
     * @param password 密码（必填，至少6位）
     * @return 注册结果JSON
     */
    @PostMapping("/register")
    public Object register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("收到注册请求，用户名：{}，邮箱：{}", registerRequest.getUsername(), registerRequest.getEmail());
        return userService.register(registerRequest);
    }

    /**
     * 修改密码接口
     * @param username 用户名（必填，用于身份验证）
     * @param email 邮箱（必填，用于身份验证）
     * @param oldPassword 旧密码（必填，至少 6 位，用于验证身份）
     * @param newPassword 新密码（必填，至少 6 位，将替换旧密码）
     * @return 密码修改结果 JSON
     */
    @PostMapping("/change-password")
    public Object changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("收到密码修改请求，用户名：{}", changePasswordRequest.getUsername());
        return userService.changePassword(changePasswordRequest);
    }

    /**
     * 退出登录接口
     * @return 退出结果 JSON
     */
    @PostMapping("/logout")
    public Object logout() {
        log.info("收到退出登录请求");
        return userService.logout();
    }
}