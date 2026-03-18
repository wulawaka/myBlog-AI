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
import java.util.ArrayList;
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
            // 解析 categoryIds 参数（多个主标签筛选）
            List<Long> categoryIdList = null;
            
            if (request.getCategoryIds() != null && !request.getCategoryIds().trim().isEmpty()) {
                String[] ids = request.getCategoryIds().split(",");
                categoryIdList = new ArrayList<>();
                
                for (String id : ids) {
                    try {
                        Long cid = Long.parseLong(id.trim());
                        categoryIdList.add(cid);
                    } catch (NumberFormatException e) {
                        return ApiResponse.error("分类 ID 格式不正确：" + id);
                    }
                }
                
                if (categoryIdList.isEmpty()) {
                    return ApiResponse.error("分类 ID 列表不能为空");
                }
            }
            
            // 如果传入了 categoryId，验证其有效性
            if (categoryIdList != null) {
                for (Long categoryId : categoryIdList) {
                    Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                    if (categoryOpt.isEmpty()) {
                        return ApiResponse.error("分类不存在，ID: " + categoryId);
                    }
                    
                    Category category = categoryOpt.get();
                    if (!category.getParentId().equals(0L)) {
                        return ApiResponse.error("分类必须是主标签，ID: " + categoryId);
                    }
                }
            }
            
            // 解析主标签 ID 和子标签 ID 列表（联动筛选）
            Long mainCategoryId = request.getCategoryId();
            List<Long> subCategoryIdList = null;
            
            if (request.getScategoryIds() != null && !request.getScategoryIds().trim().isEmpty()) {
                String[] subIds = request.getScategoryIds().split(",");
                subCategoryIdList = new ArrayList<>();
                
                for (String subId : subIds) {
                    try {
                        Long subCid = Long.parseLong(subId.trim());
                        subCategoryIdList.add(subCid);
                        log.info("解析子标签 ID: {} -> {}", subId, subCid);
                    } catch (NumberFormatException e) {
                        return ApiResponse.error("子标签 ID 格式不正确：" + subId);
                    }
                }
                
                if (subCategoryIdList.isEmpty()) {
                    return ApiResponse.error("子标签 ID 列表不能为空");
                }
                
                log.info("最终解析的子标签 ID 列表：{}", subCategoryIdList);
            } else {
                log.info("未传入子标签参数或为空字符串");
            }
            
            // 验证主标签和子标签的有效性（如果传入了）
            if (mainCategoryId != null) {
                Optional<Category> mainCategoryOpt = categoryRepository.findById(mainCategoryId);
                if (mainCategoryOpt.isEmpty()) {
                    return ApiResponse.error("主标签不存在，ID: " + mainCategoryId);
                }
                
                Category mainCategory = mainCategoryOpt.get();
                if (!mainCategory.getParentId().equals(0L)) {
                    return ApiResponse.error("主标签必须是主分类，ID: " + mainCategoryId);
                }
                
                // 如果传入了子标签，验证子标签
                if (subCategoryIdList != null && !subCategoryIdList.isEmpty()) {
                    // 先获取该主标签下的所有子标签 ID
                    List<Category> allSubCategories = categoryRepository.findByParentId(mainCategoryId);
                    List<Long> validSubCategoryIds = allSubCategories.stream()
                        .map(Category::getId)
                        .collect(Collectors.toList());
                    
                    log.info("主标签 {} 下的所有子标签 ID 列表：{}", mainCategoryId, validSubCategoryIds);
                    
                    // 验证传入的子标签是否都属于该主标签
                    for (Long subCategoryId : subCategoryIdList) {
                        Optional<Category> subCategoryOpt = categoryRepository.findById(subCategoryId);
                        if (subCategoryOpt.isEmpty()) {
                            return ApiResponse.error("子标签不存在，ID: " + subCategoryId);
                        }
                        
                        Category subCategory = subCategoryOpt.get();
                        if (subCategory.getParentId().equals(0L)) {
                            return ApiResponse.error("子标签不能是主分类，ID: " + subCategoryId);
                        }
                        
                        // 关键校验：检查子标签是否属于当前主标签
                        if (!subCategory.getParentId().equals(mainCategoryId)) {
                            return ApiResponse.error(
                                String.format("子标签 ID %d 不属于主标签 ID %d，其所属主标签 ID 为 %d", 
                                    subCategoryId, mainCategoryId, subCategory.getParentId()));
                        }
                    }
                    
                    log.info("主标签 {} 和子标签 {} 校验通过", mainCategoryId, subCategoryIdList);
                }
            }
            
            // 创建分页对象
            PageRequest pageRequest = PageRequest.of(
                request.getPageNum() - 1, 
                request.getPageSize()
            );
            
            // 根据是否有分类筛选执行不同的查询
            Page<Article> articlePage;
            
            // 优先处理主标签 + 子标签联动筛选
            // 注意：必须同时满足主标签不为 null，且子标签列表真正有值
            if (mainCategoryId != null && request.getScategoryIds() != null 
                && !request.getScategoryIds().trim().isEmpty() 
                && subCategoryIdList != null && !subCategoryIdList.isEmpty()) {
                
                log.info("进入主标签 + 子标签联动筛选模式，主标签 ID: {}, 子标签 ID 列表：{}", 
                         mainCategoryId, subCategoryIdList);
                
                // 模式：主标签 + 子标签联动
                // 第一步：查询符合主标签的所有文章 ID
                List<Article> articlesByMainCategory = articleRepository.findByCategoryIdAndIsDeletedAndIsDraft(
                    mainCategoryId, 0, 0
                );
                
                log.info("主标签 {} 下的文章总数：{}", mainCategoryId, articlesByMainCategory.size());
                
                if (articlesByMainCategory.isEmpty()) {
                    // 如果没有符合主标签的文章，直接返回空结果
                    List<ArticleListItem> emptyList = new ArrayList<>();
                    Map<String, Object> result = new HashMap<>();
                    result.put("list", emptyList);
                    result.put("total", 0L);
                    result.put("pageNum", request.getPageNum());
                    result.put("pageSize", request.getPageSize());
                    result.put("totalPages", 0);
                    return ApiResponse.success(result);
                }
                
                // 第二步：获取这些文章的 ID 列表
                List<Long> articleIds = articlesByMainCategory.stream()
                    .map(Article::getId)
                    .collect(Collectors.toList());
                
                log.info("主标签 {} 下的文章 ID 列表：{}", mainCategoryId, articleIds);
                
                // 第三步：在关联表中查找包含指定子标签的文章 ID
                List<Long> matchedArticleIds = articleCategoryRelationRepository.findArticleIdsBySubCategoryIdsIn(
                    articleIds, subCategoryIdList
                );
                
                log.info("匹配子标签 {} 的文章 ID 列表：{}", subCategoryIdList, matchedArticleIds);
                
                if (matchedArticleIds.isEmpty()) {
                    // 如果没有匹配的文章，直接返回空结果
                    List<ArticleListItem> emptyList = new ArrayList<>();
                    Map<String, Object> result = new HashMap<>();
                    result.put("list", emptyList);
                    result.put("total", 0L);
                    result.put("pageNum", request.getPageNum());
                    result.put("pageSize", request.getPageSize());
                    result.put("totalPages", 0);
                    return ApiResponse.success(result);
                }
                
                // 第四步：根据匹配的文章 ID 分页查询
                articlePage = articleRepository.findByIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
                    matchedArticleIds, 0, 0, pageRequest
                );
                
            } else if (mainCategoryId != null) {
                // 模式：只按主标签筛选
                articlePage = articleRepository.findByCategoryIdAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
                    mainCategoryId, 0, 0, pageRequest
                );
                
            } else if (categoryIdList != null && !categoryIdList.isEmpty()) {
                // 模式：按多个主标签筛选（兼容之前的功能）
                articlePage = articleRepository.findByCategoryIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
                    categoryIdList, 0, 0, pageRequest
                );
                
            } else {
                // 模式：无筛选条件
                articlePage = articleRepository.findByIsDeletedAndIsDraftOrderByUpdatedAtDesc(0, 0, pageRequest);
            }
            
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