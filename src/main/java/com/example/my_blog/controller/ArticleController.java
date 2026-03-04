package com.example.my_blog.controller;

import com.example.my_blog.dto.CreateArticleRequest;
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
     * @param categoryId 分类 ID（必填）
     * @param title 文章标题（必填，最多 200 字符）
     * @param summary 文章概述（必填，最多 500 字符）
     * @param content 文章内容（必填）
     * @param isTop 是否置顶（可选，默认 0-否，1-是）
     * @param isDraft 是否草稿（可选，默认 0-已发布，1-草稿）
     * @param isDeleted 是否删除（可选，默认 0-否，1-是）
     * @return 创建结果 JSON
     */
    @PostMapping
    public Object createArticle(@Valid @RequestBody CreateArticleRequest request) {
        log.info("收到创建文章请求，标题：{}，用户ID：{}", request.getTitle(), request.getUserId());
        return articleService.createArticle(request);
    }
}