package com.example.my_blog.service.impl;

import com.example.my_blog.common.ApiResponse;
import com.example.my_blog.dto.CreateArticleRequest;
import com.example.my_blog.dto.ArticleResponse;
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
import org.springframework.stereotype.Service;
import java.util.Optional;

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