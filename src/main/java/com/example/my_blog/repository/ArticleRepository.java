package com.example.my_blog.repository;

import com.example.my_blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 文章仓库接口
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    
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
     * 根据用户 ID 和主标签 ID 查询未删除且已发布的文章（不分页）
     */
    List<Article> findByUserIdAndCategoryIdAndIsDeletedAndIsDraft(
        Long userId, Long categoryId, Integer isDeleted, Integer isDraft
    );
    
    /**
     * 根据文章 ID 列表分页查询未删除且已发布的文章
     */
    Page<Article> findByIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        List<Long> ids, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据用户 ID 和文章 ID 列表分页查询未删除且已发布的文章
     */
    Page<Article> findByUserIdAndIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        Long userId, List<Long> ids, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据用户 ID 和主标签 ID 分页查询未删除且已发布的文章
     */
    Page<Article> findByUserIdAndCategoryIdAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        Long userId, Long categoryId, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据用户 ID 和分类 ID 列表分页查询未删除且已发布的文章
     */
    Page<Article> findByUserIdAndCategoryIdInAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        Long userId, List<Long> categoryId, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据用户 ID 分页查询未删除且已发布的文章
     */
    Page<Article> findByUserIdAndIsDeletedAndIsDraftOrderByUpdatedAtDesc(
        Long userId, Integer isDeleted, Integer isDraft, Pageable pageable
    );
    
    /**
     * 根据用户 ID 和文章 ID 查询文章（用于校验权限）
     */
    Article findByIdAndUserId(Long id, Long userId);
    
    /**
     * 检查指定分类下是否存在未删除的文章
     */
    boolean existsByCategoryIdAndIsDeleted(Long categoryId, Integer isDeleted);
    
    /**
     * 根据用户 ID 和文章 ID 查询文章（用于物理删除前的校验）
     */
    Optional<Article> findByUserIdAndId(Long userId, Long id);
}