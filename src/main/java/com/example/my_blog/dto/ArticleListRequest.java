package com.example.my_blog.dto;

import lombok.Data;

/**
 * 文章列表请求 DTO
 */
@Data
public class ArticleListRequest {
    
    /**
     * 页码（可选，默认 1）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量（可选，默认 10）
     */
    private Integer pageSize = 10;
    
    /**
     * 大标签 ID 列表（可选，逗号分隔的字符串，如 "1,2,3"）
     */
    private String categoryIds;
    
    /**
     * 主标签 ID（可选，单个，用于与子标签联动筛选）
     */
    private Long categoryId;
    
    /**
     * 子标签 ID 列表（可选，逗号分隔的字符串，如 "2,3,4"）
     */
    private String scategoryIds;
}