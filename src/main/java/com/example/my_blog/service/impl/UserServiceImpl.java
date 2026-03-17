package com.example.my_blog.service.impl;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.LoginRequest;
import com.example.my_blog.dto.RegisterRequest;
import com.example.my_blog.dto.ChangePasswordRequest;
import com.example.my_blog.entity.User;
import com.example.my_blog.repository.UserRepository;
import com.example.my_blog.service.UserService;
import com.example.my_blog.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Object login(LoginRequest loginRequest) {
        try {
            // 查找用户
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("用户不存在");
            }

            User user = userOptional.get();
            
            // 使用BCrypt校验密码
            try {
                if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    return ApiResponse.error("密码错误");
                }
            } catch (Exception e) {
                log.error("密码校验失败", e);
                return ApiResponse.error("密码校验失败");
            }

            // 登录成功，返回用户信息（敏感信息不返回）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("email", user.getEmail());
            
            // 生成 JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());

            log.info("用户 {} 登录成功", user.getUsername());
            return ApiResponse.custom(200, "登录成功", Map.of(
                "userInfo", userInfo,
                "token", token
            ));

        } catch (Exception e) {
            log.error("登录异常", e);
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    @Override
    public Object register(RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ApiResponse.error("用户名已存在");
            }
            
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ApiResponse.error("邮箱已被注册");
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            
            // 使用BCrypt加密密码
            try {
                String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
                newUser.setPassword(encodedPassword);
            } catch (Exception e) {
                log.error("密码加密失败", e);
                return ApiResponse.error("密码加密失败");
            }
            
            newUser.setNickname(registerRequest.getUsername()); // nickname和username保持一致
            newUser.setEmail(registerRequest.getEmail());
            // createdAt会在@PrePersist中自动设置

            // 保存用户
            User savedUser = userRepository.save(newUser);
            
            // 返回注册成功的用户信息（不包含密码）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", savedUser.getId());
            userInfo.put("username", savedUser.getUsername());
            userInfo.put("nickname", savedUser.getNickname());
            userInfo.put("email", savedUser.getEmail());
            userInfo.put("createdAt", savedUser.getCreatedAt());

            log.info("用户 {} 注册成功", savedUser.getUsername());
            return ApiResponse.success(userInfo);

        } catch (Exception e) {
            log.error("注册异常", e);
            return ApiResponse.error("注册失败：" + e.getMessage());
        }
    }

    @Override
    public Object changePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            // 根据用户名和邮箱查找用户
            Optional<User> userOptional = userRepository.findByUsername(changePasswordRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("用户不存在");
            }

            User user = userOptional.get();
            
            // 验证邮箱是否匹配
            if (!user.getEmail().equals(changePasswordRequest.getEmail())) {
                return ApiResponse.error("邮箱地址不正确");
            }

            // 验证旧密码是否正确
            try {
                if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
                    return ApiResponse.error("旧密码错误");
                }
            } catch (Exception e) {
                log.error("旧密码校验失败", e);
                return ApiResponse.error("密码校验失败");
            }

            // 加密新密码
            String encodedNewPassword;
            try {
                encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
            } catch (Exception e) {
                log.error("新密码加密失败", e);
                return ApiResponse.error("密码加密失败");
            }

            // 更新密码
            user.setPassword(encodedNewPassword);
            userRepository.save(user);

            log.info("用户 {} 密码修改成功", user.getUsername());
            return ApiResponse.success("密码修改成功");

        } catch (Exception e) {
            log.error("密码修改异常", e);
            return ApiResponse.error("密码修改失败：" + e.getMessage());
        }
    }
}