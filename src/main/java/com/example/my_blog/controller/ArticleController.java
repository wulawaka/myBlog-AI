package com.example.my_blog.controller;

import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.service.ArticleService;
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
     * @return 文章列表 JSON
     */
    @GetMapping("/list")
    public Object getArticleList(ArticleListRequest request) {
        log.info("收到获取文章列表请求，页码：{}，每页数量：{}", request.getPageNum(), request.getPageSize());
        return articleService.getArticleList(request);
    }
}