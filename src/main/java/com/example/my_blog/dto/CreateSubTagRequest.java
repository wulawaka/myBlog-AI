package com.example.my_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增小标签请求DTO
 */
@Data
public class CreateSubTagRequest {

    @NotNull(message = "父标签ID不能为空")
    private Long parentId;

    @NotBlank(message = "标签名称不能为空")
    @Size(min = 1, max = 6, message = "标签名称长度必须在1-6位之间")
    private String name;
}