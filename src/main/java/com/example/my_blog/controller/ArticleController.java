package com.example.my_blog.controller;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.dto.UpdateArticleTopRequest;
import com.example.my_blog.dto.UpdateArticleDraftRequest;
import com.example.my_blog.dto.ArticleStatusRequest;
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
     * 分页获取文章列表接口（无需登录）
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
     * 分页获取当前登录用户的文章列表（需要登录）
     * @param request HTTP 请求（用于获取当前登录用户 ID）
     * @param pageNum 页码（可选，默认 1）
     * @param pageSize 每页数量（可选，默认 10）
     * @param categoryIds 大标签 ID 列表（可选，逗号分隔的字符串，如 "1,2,3"）
     * @param categoryId 主标签 ID（可选，单个，用于与子标签联动筛选）
     * @param scategoryIds 子标签 ID 列表（可选，逗号分隔的字符串，如 "2,3,4"）
     * @return 文章列表 JSON
     */
    @GetMapping("/my-list")
    public Object getMyArticleList(HttpServletRequest request,
                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                   @RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "scategoryIds", required = false) String scategoryIds) {
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) request.getAttribute("userId");
        log.info("收到获取我的文章列表请求，用户 ID: {}，页码：{}，每页数量：{}", currentUserId, pageNum, pageSize);
        
        // 构建请求对象
        ArticleListRequest articleListRequest = new ArticleListRequest();
        articleListRequest.setUserId(currentUserId);
        articleListRequest.setPageNum(pageNum);
        articleListRequest.setPageSize(pageSize);
        articleListRequest.setCategoryIds(categoryIds);
        articleListRequest.setCategoryId(categoryId);
        articleListRequest.setScategoryIds(scategoryIds);
        
        return articleService.getArticleList(articleListRequest);
    }

    /**
     * 删除文章接口
     * @param id 文章 ID（路径参数）
     * @param request HTTP 请求（用于获取当前登录用户 ID）
     * @return 删除结果 JSON
     */
    @DeleteMapping("/delete/{id}")
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
        // 打印调试信息
        System.out.println("===========================================");
        System.out.println("=== Controller 收到请求 ===");
        System.out.println("===========================================");
        
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) httpServletRequest.getAttribute("userId");
        log.info("收到更新文章置顶状态请求，文章 ID：{}，置顶状态：{}，当前用户 ID：{}", 
                 request.getArticleId(), request.getIsTop(), currentUserId);
        
        // 调试：打印所有 request attributes
        log.info("Request Attributes: {}", httpServletRequest.getAttributeNames());
        
        if (currentUserId == null) {
            log.error("用户 ID 为 null，请检查是否携带有效的 Token");
            System.out.println("!!! 警告：userId 为 null，拦截器未设置用户 ID !!!");
            return ApiResponse.error("请先登录");
        }
        
        System.out.println("=== userId: " + currentUserId + " ===");
        System.out.println("===========================================");
        
        return articleService.updateArticleTop(request, currentUserId);
    }
    
    /**
     * 设置文章草稿状态接口
     * @param request 更新草稿状态请求体（包含 articleId 和 isDraft）
     * @param httpServletRequest HTTP 请求（用于获取当前登录用户 ID）
     * @return 更新结果 JSON
     */
    @PutMapping("/draft")
    public Object updateArticleDraft(@RequestBody UpdateArticleDraftRequest request, 
                                     HttpServletRequest httpServletRequest) {
        // 打印调试信息
        System.out.println("===========================================");
        System.out.println("=== 设置草稿状态 - Controller 收到请求 ===");
        System.out.println("===========================================");
        
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) httpServletRequest.getAttribute("userId");
        log.info("收到更新文章草稿状态请求，文章 ID：{}，草稿状态：{}，当前用户 ID：{}", 
                 request.getArticleId(), request.getIsDraft(), currentUserId);
        
        if (currentUserId == null) {
            log.error("用户 ID 为 null，请检查是否携带有效的 Token");
            System.out.println("!!! 警告：userId 为 null，拦截器未设置用户 ID !!!");
            return ApiResponse.error("请先登录");
        }
        
        System.out.println("=== userId: " + currentUserId + " ===");
        System.out.println("===========================================");
        
        return articleService.updateArticleDraft(request, currentUserId);
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
    
    /**
     * 分页查询文章状态接口（需要登录）
     * @param request 文章状态查询请求体（包含 articleId、isDraft、isDeleted（可选）、pageNum、pageSize）
     * @param httpServletRequest HTTP 请求（用于获取当前登录用户 ID）
     * @return 文章状态列表 JSON
     */
    @PostMapping("/status")
    public Object getArticleStatus(@Valid @RequestBody ArticleStatusRequest request,
                                   HttpServletRequest httpServletRequest) {
        // 从 request 上下文中获取当前登录用户 ID（由拦截器设置）
        Long currentUserId = (Long) httpServletRequest.getAttribute("userId");
        log.info("收到查询文章状态请求，文章 ID：{}，当前用户 ID：{}，页码：{}，每页数量：{}", 
                 request.getArticleId(), currentUserId, request.getPageNum(), request.getPageSize());
        
        if (currentUserId == null) {
            log.error("用户 ID 为 null，请检查是否携带有效的 Token");
            return ApiResponse.error("请先登录");
        }
        
        return articleService.getArticleStatus(request, currentUserId);
    }
}