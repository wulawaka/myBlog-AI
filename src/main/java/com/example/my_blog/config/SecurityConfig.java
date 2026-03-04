package com.example.my_blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置BCryptPasswordEncoder Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/user/login", "/api/user/register", "/api/user/change-password").permitAll() // 允许用户相关接口
                .requestMatchers("/api/category/**").permitAll() // 允许分类相关接口
                .requestMatchers("/api/article/**").permitAll() // 允许文章相关接口
                .anyRequest().authenticated() // 其他请求需要认证
            );
        return http.build();
    }
}