package com.example.my_blog.repository;

import com.example.my_blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文章仓库接口
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}