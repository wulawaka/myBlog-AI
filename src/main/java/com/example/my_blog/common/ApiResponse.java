package com.example.my_blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 API 响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS) // 始终包含所有字段，即使值为 null
public class ApiResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(20101, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(20101, "操作成功", null);
    }

    /**
     * 失败响应（使用默认错误码 50101）
     */
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(50101, msg, null);
    }
    
    /**
     * 失败响应（使用自定义错误码）
     */
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    /**
     * 自定义响应
     */
    public static <T> ApiResponse<T> custom(Integer code, String msg, T data) {
        return new ApiResponse<>(code, msg, data);
    }
}