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
                        "/api/article",  // 创建和更新文章需要登录
                        "/api/article/my-list",
                        "/api/article/top",
                        "/api/article/draft",
                        "/api/article/status",  // 查询文章状态需要登录
                        "/api/category/main-tag",
                        "/api/category/sub-tag",
                        "/api/category/tag",
                        "/api/category/change-tag-name",  // 更新主标签需要登录
                        "/api/article/delete/{id}",
                        "/api/article/permanent/{id}",  // 物理删除文章需要登录
                        "/api/article/restore/{id}"  // 恢复文章需要登录
                )
                .excludePathPatterns(
                        "/api/article/list",
                        "/api/user/login",
                        "/api/user/register",
                        "/api/category/tags/tree",
                        "/api/oss/**"  // 放行 OSS 相关接口
                );
    }
}