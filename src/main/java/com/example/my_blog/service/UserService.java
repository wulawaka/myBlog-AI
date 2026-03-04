package com.example.my_blog.service;

import com.example.my_blog.dto.LoginRequest;
import com.example.my_blog.dto.RegisterRequest;
import com.example.my_blog.dto.ChangePasswordRequest;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    Object login(LoginRequest loginRequest);
    
    /**
     * 用户注册
     */
    Object register(RegisterRequest registerRequest);
    
    /**
     * 修改密码
     */
    Object changePassword(ChangePasswordRequest changePasswordRequest);
}