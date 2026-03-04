package com.example.my_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增主标签请求DTO
 */
@Data
public class CreateMainTagRequest {

    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 6, message = "标签名称长度必须在1-6位之间")
    private String name;
}