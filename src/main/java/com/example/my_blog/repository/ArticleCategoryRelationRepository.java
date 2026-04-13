package com.example.my_blog.repository;

import com.example.my_blog.entity.ArticleCategoryRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 文章分类关联仓库接口
 */
@Repository
public interface ArticleCategoryRelationRepository extends JpaRepository<ArticleCategoryRelation, Long> {
    
    /**
     * 根据文章 ID 查找所有关联记录
     */
    List<ArticleCategoryRelation> findByArticleId(Long articleId);
    
    /**
     * 根据文章 ID 删除所有关联记录
     */
    void deleteByArticleId(Long articleId);
    
    /**
     * 根据文章 ID 列表和子标签 ID 列表查找匹配的文章 ID（去重）
     */
    @Query("SELECT DISTINCT r.articleId FROM ArticleCategoryRelation r " +
           "WHERE r.articleId IN :articleIds AND r.categoryId IN :subCategoryIds")
    List<Long> findArticleIdsBySubCategoryIdsIn(
        @Param("articleIds") List<Long> articleIds,
        @Param("subCategoryIds") List<Long> subCategoryIds
    );
    
    /**
     * 检查指定分类 ID 是否存在关联记录
     */
    boolean existsByCategoryId(Long categoryId);
}