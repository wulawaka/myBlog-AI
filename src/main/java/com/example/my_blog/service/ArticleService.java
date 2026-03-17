package com.example.my_blog.service;

import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;

/**
 * 文章服务接口
 */
public interface ArticleService {
    
    /**
     * 创建文章
     */
    Object createArticle(CreateArticleRequest request);
    
    /**
     * 分页获取文章列表
     */
    Object getArticleList(ArticleListRequest request);
    
    /**
     * 删除文章
     */
    Object deleteArticle(Long articleId, Long currentUserId);
}