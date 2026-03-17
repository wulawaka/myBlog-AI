package com.example.my_blog.repository;

import com.example.my_blog.entity.ArticleCategoryRelation;
import org.springframework.data.jpa.repository.JpaRepository;
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
}