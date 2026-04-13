package com.example.my_blog.repository;

import com.example.my_blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 分类仓库接口
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据父分类ID查找分类列表
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * 根据名称查找分类
     */
    Optional<Category> findByName(String name);
    
    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查找所有主标签（parent_id = 0）
     */
    List<Category> findByParentIdOrderByCreatedAtDesc(Long parentId);
    
    /**
     * 查找未删除的分类
     */
    List<Category> findByIsDeletedOrderByCreatedAtDesc(Integer isDeleted);
    
    /**
     * 根据父ID和删除状态查找分类
     */
    List<Category> findByParentIdAndIsDeletedOrderByCreatedAtDesc(Long parentId, Integer isDeleted);
    
    /**
     * 统计指定父标签下未删除的子标签数量
     */
    long countByParentIdAndIsDeleted(Long parentId, Integer isDeleted);
}