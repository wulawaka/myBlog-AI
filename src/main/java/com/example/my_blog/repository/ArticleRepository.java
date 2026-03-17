package com.example.my_blog.repository;

import com.example.my_blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文章仓库接口
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 分页查询未删除且已发布的文章
     */
    Page<Article> findByIsDeletedAndIsDraftOrderByUpdatedAtDesc(Integer isDeleted, Integer isDraft, Pageable pageable);
}