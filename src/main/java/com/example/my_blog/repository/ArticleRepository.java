package com.example.my_blog.repository;

import com.example.my_blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 文章仓库接口
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 分页查询未删除且已发布的文章
     */
    Page<Article> findByIsDeletedAndIsDraftOrderByUpdatedAtDesc(Integer isDeleted, Integer isDraft, Pageable pageable);
    
    /**
     * 根据多个分类 ID 分页查询未删除且已发布的文章
     */
    Page<Article> findByCategoryIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        List<Long> categoryId, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据主标签 ID 分页查询未删除且已发布的文章
     */
    Page<Article> findByCategoryIdAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        Long categoryId, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据主标签 ID 查询未删除且已发布的文章（不分页）
     */
    List<Article> findByCategoryIdAndIsDeletedAndIsDraft(
        Long categoryId, Integer isDeleted, Integer isDraft
    );
    
    /**
     * 根据文章 ID 列表分页查询未删除且已发布的文章
     */
    Page<Article> findByIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        List<Long> ids, Integer isDeleted, Integer isDraft, Pageable pageable
    );
}