package com.example.my_blog.controller;

import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.dto.UpdateArticleTopRequest;
import com.example.my_blog.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章接口
     * @param userId 用户 ID（必填）
     * @param categoryId 分类 ID（必填，必须是主标签 parent_id=0）
     * @param title 文章标题（必填，最多 200 字符）
     * @param summary 文章概述（必填，最多 500 字符）
     * @param content 文章内容（必填）
     * @param isTop 是否置顶（可选，默认 0-否，1-是）
     * @param isDraft 是否草稿（可选，默认 0-已发布，1-草稿）
     * @param isDeleted 是否删除（可选，默认 0-否，1-是）
     * @param scategoryId 子标签ID 字符串（可选，逗号分隔，最多 5 个）
     * @return 创建结果 JSON
     */
    @PostMapping
    public Object createArticle(@Valid @RequestBody CreateArticleRequest request) {
        log.info("收到创建文章请求，标题：{}，用户 ID：{}", request.getTitle(), request.getUserId());
        return articleService.createArticle(request);
    }
    
    /**
     * 分页获取文章列表接口
     * @param pageNum 页码（可选，默认 1）
     * @param pageSize 每页数量（可选，默认 10）
     * @param categoryIds 大标签 ID 列表（可选，逗号分隔的字符串，如 "1,2,3"）
     * @param categoryId 主标签 ID（可选，单个，用于与子标签联动筛选）
     * @param scategoryIds 子标签 ID 列表（可选，逗号分隔的字符串，如 "2,3,4"）
     * @return 文章列表 JSON
     */
    @GetMapping("/list")
    public Object getArticleList(ArticleListRequest request) {
        log.info("收到获取文章列表请求，页码：{}，每页数量：{}，分类 IDs: {}, 主标签 ID: {}, 子标签 IDs: {}", 
                 request.getPageNum(), request.getPageSize(), 
                 request.getCategoryIds(), request.getCategoryId(), request.getScategoryIds());
        return articleService.getArticleList(request);
    }

    /**
     * 删除文章接口
     * @param id 文章 ID（路径参数）
     * @param request HTTP 请求（用于获取当前登录用户 ID）
     * @return 删除结果 JSON
     */
    @DeleteMapping("/{id}")
    public Object deleteArticle(@PathVariable Long id, HttpServletRequest request) {
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) request.getAttribute("userId");
        log.info("收到删除文章请求，文章 ID：{}，当前用户 ID：{}", id, currentUserId);
        return articleService.deleteArticle(id, currentUserId);
    }

    /**
     * 设置文章置顶状态接口
     * @param request 更新置顶状态请求体（包含 articleId 和 isTop）
     * @param httpServletRequest HTTP 请求（用于获取当前登录用户 ID）
     * @return 更新结果 JSON
     */
    @PutMapping("/top")
    public Object updateArticleTop(@RequestBody UpdateArticleTopRequest request, 
                                   HttpServletRequest httpServletRequest) {
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) httpServletRequest.getAttribute("userId");
        log.info("收到更新文章置顶状态请求，文章 ID：{}，置顶状态：{}，当前用户 ID：{}", 
                 request.getArticleId(), request.getIsTop(), currentUserId);
        return articleService.updateArticleTop(request, currentUserId);
    }
    
    /**
     * 获取文章详情接口（无需登录）
     * @param id 文章 ID（路径参数）
     * @return 文章详情 JSON
     */
    @GetMapping("/{id}")
    public Object getArticleDetail(@PathVariable Long id) {
        log.info("收到获取文章详情请求，文章 ID：{}", id);
        return articleService.getArticleDetail(id);
    }
}