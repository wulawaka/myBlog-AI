package com.example.my_blog.dto;

import lombok.Data;

/**
 * 更新文章置顶状态请求 DTO
 */
@Data
public class UpdateArticleTopRequest {
    /**
     * 文章 ID
     */
    private Long articleId;
    
    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isTop;
}
