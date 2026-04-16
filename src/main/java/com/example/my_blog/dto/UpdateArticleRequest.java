package com.example.my_blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新文章请求DTO
 */
@Data
public class UpdateArticleRequest {

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200位")
    private String title;

    @NotBlank(message = "文章概述不能为空")
    @Size(max = 500, message = "文章概述长度不能超过500位")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private Integer isTop = 0;

    private Integer isDraft = 0;

    private Integer isDeleted = 0;

    private String scategoryId;
}
