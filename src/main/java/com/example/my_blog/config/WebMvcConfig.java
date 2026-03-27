package com.example.my_blog.config;

import com.example.my_blog.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/api/user/change-password",
                        "/api/user/logout",
                        "/api/article/my-list",
                        "/api/article/top",
                        "/api/article/draft",
                        "/api/category/main-tag",
                        "/api/category/sub-tag",
                        "/api/category/tag"
                )
                .excludePathPatterns(
                        "/api/article/list",
                        "/api/user/login",
                        "/api/user/register",
                        "/api/category/tags/tree"
                );
    }
}