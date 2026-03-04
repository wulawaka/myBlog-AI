package com.example.my_blog.service;

import com.example.my_blog.dto.CreateArticleRequest;

/**
 * 文章服务接口
 */
public interface ArticleService {
    
    /**
     * 创建文章
     */
    Object createArticle(CreateArticleRequest request);
}