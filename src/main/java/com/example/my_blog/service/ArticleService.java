package com.example.my_blog.service;

import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.dto.UpdateArticleTopRequest;
import com.example.my_blog.dto.UpdateArticleDraftRequest;

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
    
    /**
     * 设置文章置顶状态
     */
    Object updateArticleTop(UpdateArticleTopRequest request, Long currentUserId);
    
    /**
     * 设置文章草稿状态
     */
    Object updateArticleDraft(UpdateArticleDraftRequest request, Long currentUserId);
    
    /**
     * 获取文章详情（无需登录）
     */
    Object getArticleDetail(Long id);
}