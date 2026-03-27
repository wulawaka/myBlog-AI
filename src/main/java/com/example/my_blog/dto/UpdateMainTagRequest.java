package com.example.my_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新主标签名称请求 DTO
 */
@Data
public class UpdateMainTagRequest {

    @NotNull(message = "标签 ID 不能为空")
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 6, message = "标签名称长度必须在 1-6 位之间")
    private String name;
}
