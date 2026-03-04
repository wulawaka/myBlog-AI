package com.example.my_blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除标签请求DTO
 */
@Data
public class DeleteTagRequest {

    @NotBlank(message = "标签名称不能为空")
    private String name;
}