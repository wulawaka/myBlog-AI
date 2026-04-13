package com.example.my_blog.service;

import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.dto.UpdateArticleTopRequest;
import com.example.my_blog.dto.UpdateArticleDraftRequest;
import com.example.my_blog.dto.ArticleStatusRequest;

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
    
    /**
     * 分页查询文章状态（需要登录）
     */
    Object getArticleStatus(ArticleStatusRequest request, Long currentUserId);
    
    /**
     * 物理删除文章（彻底删除，需要登录且仅允许删除已软删除的文章）
     */
    Object permanentDeleteArticle(Long articleId, Long currentUserId);
    
    /**
     * 恢复文章（将 is_deleted 设为 0，需要登录）
     */
    Object restoreArticle(Long articleId, Long currentUserId);
}