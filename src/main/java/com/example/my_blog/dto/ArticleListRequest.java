package com.example.my_blog.dto;

import lombok.Data;

/**
 * 文章列表请求 DTO
 */
@Data
public class ArticleListRequest {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
}