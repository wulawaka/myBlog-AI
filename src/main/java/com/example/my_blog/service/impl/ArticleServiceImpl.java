package com.example.my_blog.service.impl;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleResponse;
import com.example.my_blog.dto.ArticleListRequest;
import com.example.my_blog.dto.ArticleListItem;
import com.example.my_blog.dto.UpdateArticleTopRequest;
import com.example.my_blog.entity.Article;
import com.example.my_blog.entity.User;
import com.example.my_blog.entity.Category;
import com.example.my_blog.entity.ArticleCategoryRelation;
import com.example.my_blog.repository.ArticleRepository;
import com.example.my_blog.repository.UserRepository;
import com.example.my_blog.repository.CategoryRepository;
import com.example.my_blog.repository.ArticleCategoryRelationRepository;
import com.example.my_blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleCategoryRelationRepository articleCategoryRelationRepository;

    @Override
    public Object createArticle(CreateArticleRequest request) {
        try {
            // 验证用户是否存在
            Optional<User> userOptional = userRepository.findById(request.getUserId());
            if (userOptional.isEmpty()) {
                return ApiResponse.error("用户不存在");
            }

            // 验证分类 ID 是否存在且为主标签（parent_id = 0）
            Optional<Category> categoryOptional = categoryRepository.findById(request.getCategoryId());
            if (categoryOptional.isEmpty()) {
                return ApiResponse.error("分类不存在");
            }
            
            Category category = categoryOptional.get();
            if (!category.getParentId().equals(0L)) {
                return ApiResponse.error("分类必须是主标签");
            }

            // 创建文章
            Article article = new Article();
            article.setUserId(request.getUserId());
            article.setCategoryId(request.getCategoryId());
            article.setTitle(request.getTitle());
            article.setSummary(request.getSummary());
            article.setContent(request.getContent());
            article.setIsTop(request.getIsTop() != null ? request.getIsTop() : 0);
            article.setIsDraft(request.getIsDraft() != null ? request.getIsDraft() : 0);
            article.setIsDeleted(request.getIsDeleted() != null ? request.getIsDeleted() : 0);

            Article savedArticle = articleRepository.save(article);

            // 处理子标签关联关系
            if (request.getScategoryId() != null && !request.getScategoryId().trim().isEmpty()) {
                String[] subCategoryIds = request.getScategoryId().split(",");
                
                if (subCategoryIds.length > 5) {
                    return ApiResponse.error("子标签数量不能超过 5 个");
                }
                
                for (String subCategoryIdStr : subCategoryIds) {
                    try {
                        Long subCategoryId = Long.parseLong(subCategoryIdStr.trim());
                        
                        // 验证子标签是否存在且为有效子标签（parent_id != 0）
                        Optional<Category> subCategoryOptional = categoryRepository.findById(subCategoryId);
                        if (subCategoryOptional.isEmpty()) {
                            return ApiResponse.error("子标签不存在，ID: " + subCategoryId);
                        }
                        
                        Category subCategory = subCategoryOptional.get();
                        if (subCategory.getParentId().equals(0L)) {
                            return ApiResponse.error("子标签不能是主标签，ID: " + subCategoryId);
                        }
                        
                        // 创建关联关系
                        ArticleCategoryRelation relation = new ArticleCategoryRelation();
                        relation.setArticleId(savedArticle.getId());
                        relation.setCategoryId(subCategoryId);
                        articleCategoryRelationRepository.save(relation);
                        
                    } catch (NumberFormatException e) {
                        return ApiResponse.error("子标签ID 格式不正确：" + subCategoryIdStr);
                    }
                }
            }

            // 构建返回结果
            ArticleResponse response = buildArticleResponse(savedArticle, userOptional.get());
            response.setScategoryId(request.getScategoryId());
            
            log.info("文章 {} 创建成功", savedArticle.getTitle());
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("创建文章异常", e);
            return ApiResponse.error("创建文章失败：" + e.getMessage());
        }
    }

    @Override
    public Object getArticleList(ArticleListRequest request) {
        try {
            // 创建分页对象
            PageRequest pageRequest = PageRequest.of(
                request.getPageNum() - 1, 
                request.getPageSize()
            );
            
            // 分页查询未删除且已发布的文章
            Page<Article> articlePage = articleRepository.findByIsDeletedAndIsDraftOrderByUpdatedAtDesc(0, 0, pageRequest);
            
            // 构建返回结果
            List<ArticleListItem> list = articlePage.getContent().stream()
                    .map(this::convertToArticleListItem)
                    .collect(Collectors.toList());
            
            // 构建分页响应
            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", articlePage.getTotalElements());
            result.put("pageNum", request.getPageNum());
            result.put("pageSize", request.getPageSize());
            result.put("totalPages", articlePage.getTotalPages());
            
            return ApiResponse.success(result);
            
        } catch (Exception e) {
            log.error("获取文章列表异常", e);
            return ApiResponse.error("获取文章列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 将 Article 转换为 ArticleListItem
     */
    private ArticleListItem convertToArticleListItem(Article article) {
        ArticleListItem item = new ArticleListItem();
        item.setId(article.getId());
        item.setUserId(article.getUserId());
        item.setCategoryId(article.getCategoryId());
        item.setTitle(article.getTitle());
        item.setSummary(article.getSummary());
        item.setIsTop(article.getIsTop());
        item.setUpdatedAt(article.getUpdatedAt());
        
        // 获取子标签ID 列表
        List<ArticleCategoryRelation> relations = articleCategoryRelationRepository.findByArticleId(article.getId());
        String subCategoryIds = relations.stream()
                .map(relation -> relation.getCategoryId().toString())
                .collect(Collectors.joining(","));
        item.setSubCategoryIds(subCategoryIds);
        
        return item;
    }

    @Override
    public Object deleteArticle(Long articleId, Long currentUserId) {
        try {
            // 查询文章
            Optional<Article> articleOptional = articleRepository.findById(articleId);
            
            if (articleOptional.isEmpty()) {
                return ApiResponse.error("文章不存在");
            }

            Article article = articleOptional.get();
            
            // 检查文章是否已被删除
            if (article.getIsDeleted().equals(1)) {
                return ApiResponse.error("文章已被删除");
            }
            
            // 校验权限：只能删除自己的文章
            if (!article.getUserId().equals(currentUserId)) {
                return ApiResponse.error("无权删除该文章");
            }

            // 执行软删除
            article.setIsDeleted(1);
            articleRepository.save(article);

            log.info("文章 {} 被用户 {} 删除", article.getTitle(), currentUserId);
            return ApiResponse.success(null);

        } catch (Exception e) {
            log.error("删除文章异常", e);
            return ApiResponse.error("删除文章失败：" + e.getMessage());
        }
    }

    @Override
    public Object updateArticleTop(UpdateArticleTopRequest request, Long currentUserId) {
        try {
            // 验证参数
            if (request.getArticleId() == null) {
                return ApiResponse.error("文章 ID 不能为空");
            }
            
            if (request.getIsTop() == null || (request.getIsTop() != 0 && request.getIsTop() != 1)) {
                return ApiResponse.error("置顶状态必须为 0 或 1");
            }
            
            // 查询文章
            Optional<Article> articleOptional = articleRepository.findById(request.getArticleId());
            
            if (articleOptional.isEmpty()) {
                return ApiResponse.error("文章不存在");
            }

            Article article = articleOptional.get();
            
            // 检查文章是否已被删除
            if (article.getIsDeleted().equals(1)) {
                return ApiResponse.error("文章已被删除，无法设置置顶状态");
            }
            
            // 校验权限：只能修改自己的文章
            if (!article.getUserId().equals(currentUserId)) {
                return ApiResponse.error("无权修改该文章");
            }

            // 更新置顶状态（直接同步到数据库）
            article.setIsTop(request.getIsTop());
            articleRepository.save(article);

            log.info("文章 {} 置顶状态更新为：{}，操作用户：{}", article.getTitle(), request.getIsTop(), currentUserId);
            return ApiResponse.success(null);

        } catch (Exception e) {
            log.error("更新文章置顶状态异常", e);
            return ApiResponse.error("更新文章置顶状态失败：" + e.getMessage());
        }
    }

    /**
     * 构建文章响应对象
     */
    private ArticleResponse buildArticleResponse(Article article, User user) {
        ArticleResponse response = new ArticleResponse();
        
        // 文章基本信息
        response.setId(article.getId());
        response.setUserId(article.getUserId());
        response.setCategoryId(article.getCategoryId());
        response.setTitle(article.getTitle());
        response.setSummary(article.getSummary());
        response.setContent(article.getContent());
        response.setIsTop(article.getIsTop());
        response.setIsDraft(article.getIsDraft());
        response.setIsDeleted(article.getIsDeleted());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        
        // 用户信息
        ArticleResponse.UserInfo userInfo = new ArticleResponse.UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        response.setUserInfo(userInfo);
        
        return response;
    }
}