package com.example.my_blog.dto;

import lombok.Data;

/**
 * 设置文章草稿状态请求 DTO
 */
@Data
public class UpdateArticleDraftRequest {
    
    /**
     * 文章 ID
     */
    private Long articleId;
    
    /**
     * 草稿状态（0：不存草稿，1：存入草稿）
     */
    private Integer isDraft;
}
