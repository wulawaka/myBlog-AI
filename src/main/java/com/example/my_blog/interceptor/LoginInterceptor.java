package com.example.my_blog.interceptor;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 Authorization 请求头
        String authorization = request.getHeader("Authorization");
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.warn("Token 缺失或格式不正确");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":200,\"msg\":\"请先登录\",\"data\":null}");
            return false;
        }

        // 提取 Token
        String token = authorization.substring(7);
        
        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 无效或已过期");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":200,\"msg\":\"请先登录\",\"data\":null}");
            return false;
        }

        // 从 Token 中获取用户 ID，存入请求上下文
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            request.setAttribute("userId", userId);
            log.info("用户 {} 通过身份验证", userId);
        } else {
            log.warn("Token 中未找到用户 ID");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":200,\"msg\":\"请先登录\",\"data\":null}");
            return false;
        }

        return true;
    }
}